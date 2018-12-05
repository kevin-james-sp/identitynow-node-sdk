package sailpoint.engineering.perflab;

import com.jcraft.jsch.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;
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

    private static final String SSH_USERNAME = "fangmingning";
    private static final String JUMP_BOX_URL = "jb1-dev02-useast1.cloud.sailpoint.com";
    private static final String CC_INSTANCE_URL = "10.0.38.254";// Make sure the CC instance exists. It will change if it's recreated
    private static final String CC_RDS_MYSQL_URL = "dev02-useast1-cc.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com";
    private static final String CC_RDS_MYSQL_USERNAME = "admin20170151";
    private static final String CC_RDS_MYSQL_PASSWORD = "a946c53336";

    private static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";
    private static final String PERF_KBA_ANSWER = "test";

    public static void main(String[] args) throws Exception {
        Log4jUtils.boostrapLog4j(Level.INFO);

        //TODO: Concurrent driver to drive either the kba route or the code route
        twoMfaThroughKbaAnswer("1057");
    }

    private static void twoMfaThroughKbaAnswer(String username) throws Exception {

        IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
        ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);

        AccountService accountService = ids.getAccountService();

        JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, "perflab-05121458", "pswd-reset")).execute().body();

        PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();

        MFADetails mfaDetails = accountService.mfaDetails("KBA", passwordIsReady.JPT).execute().body();

        MFAChallenge kbaCityBornChallenge = mfaDetails.data.challenges.stream().filter(mfaChallenge -> mfaChallenge.text.equals("What city were you born in?")).findAny().orElse(null);

        kbaCityBornChallenge.answer = PasswordUtil.encodeSha256String(PERF_KBA_ANSWER);

        JPTResult mfaVerifyResult = accountService.mfaVerify(mfaDetails.JPT, new MFAVerify("KBA", Collections.singletonList(kbaCityBornChallenge))).execute().body();

        PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(mfaVerifyResult.JPT).execute().body();

        Org orgKeyInfo = passwordPolicy.org;

        JPTResult passwordChangeResult = accountService.pwdReset(mfaVerifyResult.JPT, new PasswordReset(username,
                PasswordUtil.encodeSha256String(PERF_DEFAULT_PWD),
                PasswordUtil.getPassthroughHash(username, orgKeyInfo.encryptionKey),
                PasswordUtil.getPassthroughHash(PERF_DEFAULT_PWD, orgKeyInfo.encryptionKey),
                orgKeyInfo.encryptionKeyId)).execute().body();

        PasswordPoll pollingResult;

        pollingResult = accountService.pwdPoll(passwordChangeResult.JPT).execute().body();

        while (!pollingResult.state.equals("FINISHED")) {
            log.info("Polling password reset result");
            Thread.sleep(9000);
            pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
        }


        log.info("Done");

    }

    private static void twoMfaThroughCode() throws Exception {//TODO: Get the code through query. Exception handling, etc.
        JSch jsch=new JSch();
        jsch.addIdentity("~/.ssh/id_rsa");

        //Connecting to jump box
        Session jumpBoxSession = jsch.getSession(SSH_USERNAME, JUMP_BOX_URL);
        jumpBoxSession.setConfig("StrictHostKeyChecking", "no");
        jumpBoxSession.connect();

        //Port forwarding to the ssh port on CC instance
        int ccSshPort = jumpBoxSession.setPortForwardingL(0, CC_INSTANCE_URL, 22);

        //Connecting to CC instance
        Session serverSession = jsch.getSession(SSH_USERNAME, "localhost", ccSshPort);
        serverSession.setConfig("StrictHostKeyChecking", "no");
        serverSession.connect();

        //Port forwarding to the mysql port on mysql instance
        int mysqlPort = serverSession.setPortForwardingL(0, CC_RDS_MYSQL_URL, 3306);

        //Run mysql query
        try {
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:" + mysqlPort, CC_RDS_MYSQL_USERNAME, CC_RDS_MYSQL_PASSWORD)){
                try (Statement statement = connection.createStatement()) {

                    ResultSet resultSet = statement.executeQuery("select description from cloudcommander.proc");
                    while(resultSet.next()) {
                        System.out.println(resultSet.getString(1));
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Disconnect session in order
        serverSession.disconnect();
        jumpBoxSession.disconnect();
    }

}
