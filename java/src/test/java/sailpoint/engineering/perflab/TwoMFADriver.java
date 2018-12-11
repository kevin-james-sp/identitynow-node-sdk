package sailpoint.engineering.perflab;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TwoMFADriver {

    private final static Logger log = LogManager.getLogger(TwoMFADriver.class);

    public static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";

    private static final String TEST_ORG = "perflab-05121458";

    private static final String PERF_KBA_ANSWER = "test";

    // Example JVM & Jenkins options: -DccDbPassword=thePassword -DpwdResetMethod=KBA_Answer -DtestUserCount=10000 -DtestThreadCount=20
    private static final String CC_DB_PASSWORD = System.getProperty("ccDbPassword");
    private static final String PWD_RESET_METHOD = System.getProperty("pwdResetMethod");
    private static final String TEST_USER_COUNT = System.getProperty("testUserCount");
    private static final String TEST_THREAD_COUNT = System.getProperty("testThreadCount");

    public static void main(String[] args) throws Exception{ //todo remove
        Log4jUtils.boostrapLog4j(Level.INFO);

        log.info("Starting 2MFA load test with " + (PWD_RESET_METHOD == null ? "KBA_Answer" : PWD_RESET_METHOD) + " for " + TEST_USER_COUNT +
                " users with " + TEST_THREAD_COUNT + " threads.");
        //twoMfaThroughKbaAnswer("10002");
        //twoMfaThroughCode("1057");

//        //TODO: Concurrent driver to drive either the kba route or the code route
//        try {
//            Integer.parseInt(TEST_USER_COUNT);
//            Integer.parseInt(TEST_THREAD_COUNT);
//        } catch (NumberFormatException e) {
//            log.error("User and thread count must be integer");
//            return;
//        }
//
//        if (PWD_RESET_METHOD == null || PWD_RESET_METHOD.equals("KBA_Answer")) {
//            twoMfaThroughKbaAnswer("10002");
//        } else {
//            if (CC_DB_PASSWORD == null) {
//                log.error("Failed to run KBA test. Please make sure \"sshUsername\", \"ccInstanceIp\" and \"ccDbPassword\" are set in system properties by JVM options");
//            } else {
//                twoMfaThroughCode("1057");
//            }
//        }

        List<MFADriver> driverList = new ArrayList<MFADriver>() {{
            add(new MFAKbaDriver());
            add(new MFAResetCodeDriver(MFAType.EMAIL_WORK, CC_DB_PASSWORD));
            add(new MFAResetCodeDriver(MFAType.EMAIL_PERSONAL, CC_DB_PASSWORD));
//            add(new MFAResetCodeDriver(MFAType.SMS_WORK, CC_DB_PASSWORD));
//            add(new MFAResetCodeDriver(MFAType.SMS_PERSONAL, CC_DB_PASSWORD));
        }};


        String username = "1057"; // todo: put in
        String successMsg = "Successfully reset password for user " + username + " with ";


        //Create account service (No session or access token)
        IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
        ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
        AccountService accountService = ids.getAccountService();

        //Request for password reset
        JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, TEST_ORG, "pswd-reset")).execute().body();
        PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();

        while (!passwordIsReady.ready && driverList.size() > 0) {
            MFADriver driver = driverList.remove((int)(Math.random() * driverList.size()));
            String jptToken = driver.execute(accountService, passwordIsReady.JPT, username);
            passwordIsReady = accountService.pwdIsReady(jptToken).execute().body();

            successMsg += driver.getMFAType().getFriendlyName() + " and ";
        }

        successMsg = successMsg.replaceAll(" and $", ".");

        //Getting password policy and send reset password request
        PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(passwordIsReady.JPT).execute().body();
        Org orgKeyInfo = passwordPolicy.org;
        JPTResult passwordChangeResult = accountService.pwdReset(passwordIsReady.JPT, new PasswordReset(username,
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
                throw new IllegalStateException("Failed while resetting password with code for " + username + "."); //todo method name
            }
            Thread.sleep(8000);
            pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
        }

        log.info(successMsg);

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

            MFAKbaDriver kbaDriver = new MFAKbaDriver();
            kbaDriver.execute(accountService, passwordIsReady.JPT, username);



            log.info("Successfully reset password for user " + username);
        } catch (NullPointerException e) {
            log.error("Failed while resetting password for " + username + ". Server response is missing required parameters.", e);
        } catch (IOException e) {
            log.error("Failed while resetting password for " + username + ". Cannot send request.", e);
        }
//        catch (InterruptedException e) {
//            log.error("Failed while resetting password for " + username + ". Cannot sleep the thread wile polling for password reset results.", e);
//        }
        catch (Exception e) {
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

            MFAResetCodeDriver resetCodeDriver = new MFAResetCodeDriver(MFAType.SMS_PERSONAL, CC_DB_PASSWORD);
            resetCodeDriver.execute(accountService, passwordIsReady.JPT, username);


            log.info("Successfully reset password for user " + username);

        } catch (NullPointerException e) {
            log.error("Failed while resetting password for " + username + ". Server response is missing required parameters.", e);
        } catch (IOException e) {
            log.error("Failed while resetting password for " + username + ". Cannot send request.", e);
        }
//        catch (InterruptedException e) {
//            log.error("Failed while resetting password for " + username + ". Cannot sleep the thread wile polling for password reset results.", e);
//        }
        catch (IllegalStateException e){
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed while resetting password for " + username + ".", e);
        }

    }

}
