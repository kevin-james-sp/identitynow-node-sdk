package sailpoint.engineering.perflab;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.account.*;
import sailpoint.services.idn.sdk.services.AccountService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.util.PasswordUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class TwoMFADriver {

    private final static Logger log = LogManager.getLogger(TwoMFADriver.class);

    private static final String TEST_ORG = "perflab-05121458";
    private static final String CC_RDS_MYSQL_URL = "dev02-useast1-cc.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com";
    private static final String CC_RDS_MYSQL_USERNAME = "admin20170151";
    private static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";
    private static final String PERF_KBA_ANSWER = "test";

    // Example JVM & Jenkins options: -DccDbPassword=thePassword -DpwdResetMethod=KBA_Answer -DtestUserCount=10000 -DtestThreadCount=20
    private static final String CC_DB_PASSWORD = System.getProperty("ccDbPassword");
    private static final String PWD_RESET_METHOD = System.getProperty("pwdResetMethod");
    private static final String TEST_USER_COUNT = System.getProperty("testUserCount");
    private static final String TEST_THREAD_COUNT = System.getProperty("testThreadCount");

    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        log.info("Starting 2MFA load test with " + (PWD_RESET_METHOD == null ? "KBA_Answer" : PWD_RESET_METHOD) + " for " + TEST_USER_COUNT +
                " users with " + TEST_THREAD_COUNT + " threads.");

        //TODO: Concurrent driver to drive either the kba route or the code route
        try {
            Integer.parseInt(TEST_USER_COUNT);
            Integer.parseInt(TEST_THREAD_COUNT);
        } catch (NumberFormatException e) {
            log.error("User and thread count must be integer");
            return;
        }

        if (PWD_RESET_METHOD == null || PWD_RESET_METHOD.equals("KBA_Answer")) {
            twoMfaThroughKbaAnswer("10002");
        } else {
            if (CC_DB_PASSWORD == null) {
                log.error("Failed to run KBA test. Please make sure \"sshUsername\", \"ccInstanceIp\" and \"ccDbPassword\" are set in system properties by JVM options");
            } else {
                twoMfaThroughCode("1057");
            }
        }

    }

    /**
     * Code to reset password using kba answer.
     * @param username the user name to reset password for
     */
    private static void twoMfaThroughKbaAnswer(String username) {

        try {
            //Create account service (No session or access token)
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccountService accountService = ids.getAccountService();

            //Password reset request with kba
            JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, TEST_ORG, "pswd-reset")).execute().body();
            PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();
            MFADetails mfaDetails = accountService.mfaDetails("KBA", passwordIsReady.JPT).execute().body();
            MFAChallenge kbaCityBornChallenge = mfaDetails.data.challenges.stream().filter(mfaChallenge -> mfaChallenge.text.equals("What city were you born in?")).findAny().orElse(null);
            kbaCityBornChallenge.answer = PasswordUtil.encodeSha256String(PERF_KBA_ANSWER);

            //Verify answer and reset password
            JPTResult mfaVerifyResult = accountService.mfaVerify(mfaDetails.JPT, new MFAVerify("KBA", Collections.singletonList(kbaCityBornChallenge))).execute().body();
            PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(mfaVerifyResult.JPT).execute().body();
            Org orgKeyInfo = passwordPolicy.org;
            JPTResult passwordChangeResult = accountService.pwdReset(mfaVerifyResult.JPT, new PasswordReset(username,
                    PasswordUtil.encodeSha256String(PERF_DEFAULT_PWD),
                    PasswordUtil.getPassthroughHash(username, orgKeyInfo.encryptionKey),
                    PasswordUtil.getPassthroughHash(PERF_DEFAULT_PWD, orgKeyInfo.encryptionKey),
                    orgKeyInfo.encryptionKeyId)).execute().body();

            //Poll for reset result. Simulating UI behavior, where it poll every 8 seconds. Error message will return if it polled 35 times without FINISH status
            PasswordPoll pollingResult;
            pollingResult = accountService.pwdPoll(passwordChangeResult.JPT).execute().body();
            int pollingCount = 0;
            while (!pollingResult.state.equals("FINISHED")) {
                if (++pollingCount > 35) {
                    log.error("Failed while resetting password with kba answer for " + username + ".");
                    return;
                }
                Thread.sleep(8000);
                pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
            }

            log.info("Successfully reset password for user " + username);
        } catch (NullPointerException e) {
            log.error("Failed while resetting password for " + username + ". Server response is missing required parameters.", e);
        } catch (IOException e) {
            log.error("Failed while resetting password for " + username + ". Cannot send request.", e);
        } catch (InterruptedException e) {
            log.error("Failed while resetting password for " + username + ". Cannot sleep the thread wile polling for password reset results.", e);
        } catch (Exception e) {
            log.error("Failed while resetting password for " + username + ".", e);
        }

    }

    /**
     * Code to reset password using password reset code.
     * NOTE: That only partial of this method is multi thread-able. Look for comments below
     * @param username the user name to reset password for
     */
    private static void twoMfaThroughCode(String username){

        try {
            //Create account service (No session or access token)
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccountService accountService = ids.getAccountService();

            //Send password reset code
            JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, TEST_ORG, "pswd-reset")).execute().body();
            PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();
            JPTResult mfaSend = accountService.mfaSend("SMS_PERSONAL", passwordIsReady.JPT).execute().body();

            //Read the code from cc database
            String query = "{\"db_user\":\"" + CC_RDS_MYSQL_USERNAME + "\",\"query\":\"select passwd_reset_key from user where alias = '" +
                    username + "' and passwd_reset_key is not null\",\"host\":\"" + CC_RDS_MYSQL_URL + "\",\"db_pass\":\"" + CC_DB_PASSWORD +
                    "\",\"db\":\"cloudcommander\"}\n";
            String passwordResetCode = null;
            AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
            InvokeRequest req = new InvokeRequest().withFunctionName("infra-db-client-dev-select").withPayload(query);
            InvokeResult requestResult = lambdaClient.invoke(req);
            ByteBuffer byteBuf = requestResult.getPayload();
            if (byteBuf != null) {
                String resetCode = "" + Integer.parseInt(StandardCharsets.UTF_8.decode(byteBuf).toString().replaceAll("[\\D]", ""));
                if (resetCode.length() != 6) {
                    throw new IllegalStateException("The password reset code from lambda is not in correct format for " + username + ". The code is " + resetCode);
                } else {
                    passwordResetCode = resetCode;
                }
            } else {
                throw new IllegalStateException("Failed to retrieve password reset code from aws lambda for " + username + ".");
            }

            //Verify answer and reset password
            MFAChallenge resetCodeChallenge = new MFAChallenge("code", passwordResetCode);
            JPTResult mfaVerifyResult = accountService.mfaVerify(mfaSend.JPT,
                    new MFAVerify("SMS_PERSONAL", Collections.singletonList(resetCodeChallenge))).execute().body();
            PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(mfaVerifyResult.JPT).execute().body();
            Org orgKeyInfo = passwordPolicy.org;
            JPTResult passwordChangeResult = accountService.pwdReset(mfaVerifyResult.JPT, new PasswordReset(username,
                    PasswordUtil.encodeSha256String(PERF_DEFAULT_PWD),
                    PasswordUtil.getPassthroughHash(username, orgKeyInfo.encryptionKey),
                    PasswordUtil.getPassthroughHash(PERF_DEFAULT_PWD, orgKeyInfo.encryptionKey),
                    orgKeyInfo.encryptionKeyId)).execute().body();

            //Poll for reset result. Simulating UI behavior, where it poll every 8 seconds. Error message will return if it polled 35 times without FINISH status
            PasswordPoll pollingResult;
            pollingResult = accountService.pwdPoll(passwordChangeResult.JPT).execute().body();
            int pollingCount = 0;
            while (!pollingResult.state.equals("FINISHED")) {
                if (++pollingCount > 35) {
                    throw new IllegalStateException("Failed while resetting password with code for " + username + ".");
                }
                Thread.sleep(8000);
                pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
            }


            log.info("Successfully reset password for user " + username);

        } catch (NullPointerException e) {
            log.error("Failed while resetting password for " + username + ". Server response is missing required parameters.", e);
        } catch (IOException e) {
            log.error("Failed while resetting password for " + username + ". Cannot send request.", e);
        } catch (InterruptedException e) {
            log.error("Failed while resetting password for " + username + ". Cannot sleep the thread wile polling for password reset results.", e);
        } catch (IllegalStateException e){
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed while resetting password for " + username + ".", e);
        }

    }

}
