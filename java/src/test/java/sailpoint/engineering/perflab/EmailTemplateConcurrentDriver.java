package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class EmailTemplateConcurrentDriver {

    private final static Logger log = LogManager.getLogger(EmailTemplateConcurrentDriver.class);

    public static void main(String[] args) throws Exception{
        Log4jUtils.boostrapLog4j(Level.INFO);

        ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();

        if (!envCreds.hasUserCredentials()) {
            log.error("No user credentials present in environment configuration st ~/.idnSdk/sdkClient.conf");
            return;
        }

        int numWorkerThreads = 12;
        log.info("Making Bulk api/emailTemplate/sendTestEmail calls into " + envCreds.getOrgName() + " using " + numWorkerThreads + " threads.");

        ConcurrentLinkedQueue<UserInterfaceSession> sessionPool = new ConcurrentLinkedQueue<UserInterfaceSession>();

        AtomicInteger desiredCalls = new AtomicInteger(1000);
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService es = Executors.newFixedThreadPool(numWorkerThreads);

        UserInterfaceSession uiSession = (UserInterfaceSession) SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
        uiSession.open();
        uiSession.getNewSessionToken();
        uiSession.stronglyAuthenticate();
        uiSession.checkTokenExpiration();

        Map<String, String> formMap = new HashMap<>();
        formMap.put("body", "not important");
        formMap.put("subject", "test subject");
        formMap.put("replyTo", "no-reply@sailpoint.com");
        formMap.put("id", "not impotant");
        formMap.put("templateName", "not important");

        String res = uiSession.doApiPost("/cc/api/emailTemplate/sendTestEmail", formMap);

        System.out.println(res);

    }
}
