package sailpoint.engineering.perflab;

import com.jcraft.jsch.*;
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
import java.sql.*;
import java.util.Collections;

public class TwoMFADriver {

    private final static Logger log = LogManager.getLogger(TwoMFADriver.class);

    private static final String TEST_ORG = "perflab-05121458";
    private static final String SSH_PRIV_KEY_FILE_PATH = "~/.ssh/id_rsa";
    private static final String JUMP_BOX_URL = "jb1-dev02-useast1.cloud.sailpoint.com";
    private static final String CC_RDS_MYSQL_URL = "dev02-useast1-cc.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com";
    private static final String CC_RDS_MYSQL_USERNAME = "admin20170151";
    private static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";
    private static final String PERF_KBA_ANSWER = "test";

    // Example JVM options: -DsshUsername=fangmingning -DccInstanceIp=10.0.38.80 -DccDbPassword=thePassword
    private static final String SSH_USERNAME = System.getProperty("sshUsername");
    private static final String CC_INSTANCE_URL = System.getProperty("ccInstanceIp");
    private static final String CC_DB_PASSWORD = System.getProperty("ccDbPassword");

    // Jenkins options. Can also be set while running locally
    private static final String PWD_RESET_METHOD = System.getProperty("pwdResetMethod");
    private static final String TEST_USER_COUNT = System.getProperty("testUserCount");

    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        log.info("Starting 2MFA load test with " + (TEST_USER_COUNT == null ? "KBA Answer" : TEST_USER_COUNT) + "with " + TEST_USER_COUNT + " users.");

        //TODO: Concurrent driver to drive either the kba route or the code route
        if (TEST_USER_COUNT == null || PWD_RESET_METHOD.equals("KBA Answer")) {
            twoMfaThroughKbaAnswer("10002");
        } else {
            if (SSH_USERNAME == null || CC_INSTANCE_URL == null || CC_DB_PASSWORD == null) {//TODO: This is only needed for reset code.
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
        Session jumpBoxSession = null;
        Session serverSession = null;

        try {
            JSch jsch=new JSch();
            jsch.addIdentity(SSH_PRIV_KEY_FILE_PATH);
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Connecting to jump box
            jumpBoxSession = jsch.getSession(SSH_USERNAME, JUMP_BOX_URL);
            jumpBoxSession.setConfig("StrictHostKeyChecking", "no");
            jumpBoxSession.connect();

            //Port forwarding to the ssh port on CC instance
            int ccSshPort = jumpBoxSession.setPortForwardingL(0, CC_INSTANCE_URL, 22);

            //Connecting to CC instance
            serverSession = jsch.getSession(SSH_USERNAME, "localhost", ccSshPort);
            serverSession.setConfig("StrictHostKeyChecking", "no");
            serverSession.connect();

            //Port forwarding to the mysql port on mysql instance
            int mysqlPort = serverSession.setPortForwardingL(0, CC_RDS_MYSQL_URL, 3306);

            /* ************************************************************************************************************
             *                                   Start of multi thread-able part
             * ************************************************************************************************************/

            //Create account service (No session or access token)
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccountService accountService = ids.getAccountService();

            //Send password reset code
            JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, TEST_ORG, "pswd-reset")).execute().body();
            PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();
            JPTResult mfaSend = accountService.mfaSend("SMS_PERSONAL", passwordIsReady.JPT).execute().body();

            //Read the code from cc database
            String passwordResetCode = null;
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:" + mysqlPort, CC_RDS_MYSQL_USERNAME, CC_DB_PASSWORD)){
                try (Statement statement = connection.createStatement()) {

                    ResultSet resultSet = statement.executeQuery("select passwd_reset_key from cloudcommander.user where alias = '"
                            + username + "' and passwd_reset_key is not null");
                    while(resultSet.next() && passwordResetCode == null) {
                        passwordResetCode = resultSet.getString(1);
                    }
                    if (passwordResetCode == null)
                        throw new IllegalStateException("Cannot find password reset code from database for " + username + ".");
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Failed while retriveing password reset code from database for " + username + ". " + e.getMessage());
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

            /* ************************************************************************************************************
             *                                   End of multi thread-able part
             * ************************************************************************************************************/

        } catch (JSchException e) {
            log.error("Failed while resetting password for " + username + ". Cannot establish ssh tunnel."
                    + "Please make sure the CC instance IP defined in local cred file exists on AWS EC2", e);
        } catch (ClassNotFoundException e) {
            log.error("Failed while resetting password for " + username + ". Cannot find mysql driver.", e);
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
        } finally {
            //Disconnect session in order
            serverSession.disconnect();
            jumpBoxSession.disconnect();
        }

    }

}
