package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.account.*;
import sailpoint.services.idn.sdk.services.AccountService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.util.PasswordUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwoMFADriver {

    private final static Logger log = LogManager.getLogger(TwoMFADriver.class);

    private static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";

    // Example JVM & Jenkins options: -DccDbPassword=thePassword -DtestUserCount=10000 -DtestThreadCount=20 -Dgoal=accnt-unlock
    private static final String CC_DB_PASSWORD = System.getProperty("ccDbPassword");
    private static final String TEST_USER_COUNT = System.getProperty("testUserCount", "1000");
    private static final String TEST_THREAD_COUNT = System.getProperty("testThreadCount", "20");
    private static final String GOAL = System.getProperty("goal", "pswd-reset");

    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        //Validate test configuration
        if (CC_DB_PASSWORD == null) {
            log.error("Failed to start 2MFA load test. Please provide CC database password through system properties (JVM options)");
        } else {
            try {
                int userCount = Integer.parseInt(TEST_USER_COUNT);
                int threadCount = Integer.parseInt(TEST_THREAD_COUNT);

                if (userCount > 0 && threadCount > 0) {
                    //Validation complete. Print start message and start
                    log.info("Starting 2MFA load test with " + TEST_USER_COUNT + " users and " + TEST_THREAD_COUNT + " threads.");

                    //TODO: Concurrent driver to drive this method, supplying the username that ranges from 1k to 10k
                    if (execute("1057")) {
                        //TODO: Add 1 to some atomic integer indicating success
                    } else {
                        //TODO: Error handling, if any
                    }
                } else {
                    log.error("Failed to start 2MFA load test. User and thread count must be POSITIVE integers");
                }

            } catch (NumberFormatException e) {
                log.error("Failed to start 2MFA load test. User and thread count must be integers");
            }
        }

    }

    /**
     * Code to reset password using "n" MFA.
     * It loops through the available MFA drivers and randomly execute for the password reset.
     * @param username the user name to reset password for
     * @return true if the reset is successful. false otherwise
     */
    private static boolean execute(String username){

        try {
            //Success message. Might be interrupted by any error message.
            String goalName = GOAL.equals("pswd-reset") ? "reset password" : "unlocked account";
            String successMsg = "Successfully " + goalName + " for user " + username + " with ";

            //Initiate list of available drivers
            List<MFADriver> driverList = new ArrayList<MFADriver>() {{
                add(new MFAKbaDriver());
                add(new MFAResetCodeDriver(MFAType.EMAIL_WORK, CC_DB_PASSWORD));
                add(new MFAResetCodeDriver(MFAType.EMAIL_PERSONAL, CC_DB_PASSWORD));
//                add(new MFAResetCodeDriver(MFAType.SMS_WORK, CC_DB_PASSWORD));
//                add(new MFAResetCodeDriver(MFAType.SMS_PERSONAL, CC_DB_PASSWORD));
            }};

            //Create account service without session or access token
            ClientCredentials clientCredentials = EnvironmentCredentialer.getEnvironmentCredentials();
            IdentityNowService ids = new IdentityNowService(clientCredentials);
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccountService accountService = ids.getAccountService();

            //Request for password reset
            JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, clientCredentials.getOrgName(), GOAL)).execute().body();
            PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();

            //Keep doing MFA until the driver list is empty or the password ready signal is returned by CC
            while (!passwordIsReady.ready && driverList.size() > 0) {
                MFADriver driver = driverList.remove((int)(Math.random() * driverList.size()));
                String jptToken = driver.execute(accountService, passwordIsReady.JPT, username);
                passwordIsReady = accountService.pwdIsReady(jptToken).execute().body();

                successMsg += driver.getMFAType().getFriendlyName() + " and ";
            }

            //Replace the last "and" for readability
            successMsg = successMsg.replaceAll(" and $", ".");

            String pollingJPTToken;
            if (GOAL.equals("pswd-reset")) {

                //Getting password policy and send reset password request
                PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(passwordIsReady.JPT).execute().body();
                Org orgKeyInfo = passwordPolicy.org;
                JPTResult passwordChangeResult = accountService.pwdReset(passwordIsReady.JPT, new PasswordReset(username,
                        PasswordUtil.encodeSha256String(PERF_DEFAULT_PWD),
                        PasswordUtil.getPassthroughHash(username, orgKeyInfo.encryptionKey),
                        PasswordUtil.getPassthroughHash(PERF_DEFAULT_PWD, orgKeyInfo.encryptionKey),
                        orgKeyInfo.encryptionKeyId)).execute().body();
                pollingJPTToken = passwordChangeResult.JPT;
            } else {
                JPTResult enableUserResult = accountService.pwdUnlock(passwordIsReady.JPT).execute().body();
                pollingJPTToken = enableUserResult.JPT;
            }

            //Poll for reset result. Simulating UI behavior, where it poll every 8 seconds. Error message will return if it polled 35 times without FINISH status
            PasswordPoll pollingResult;
            pollingResult = accountService.pwdPoll(pollingJPTToken).execute().body();
            int pollingCount = 0;
            while (!pollingResult.state.equals("FINISHED")) {
                if (++pollingCount > 35) {
                    throw new IllegalStateException("Failed while resetting password with code for " + username + " using" + successMsg.split("with", 2)[1] +
                            " Polling result for 35 times without receiving a finished message.");
                } else if (pollingResult.JPT == null) {
                    throw new IllegalStateException("Failed while resetting password with code for " + username + " using" + successMsg.split("with", 2)[1] +
                            " Polling returns error message: " + pollingResult.statusMessage);
                }
                Thread.sleep(8000);
                pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
            }

            log.info(successMsg);
            return true;

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

        return false;

    }

}
