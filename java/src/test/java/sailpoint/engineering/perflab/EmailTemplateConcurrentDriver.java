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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

        int numWorkerThreads = 5;
        log.info("Making Bulk api/emailTemplate/sendTestEmail calls into " + envCreds.getOrgName() + " using " + numWorkerThreads + " threads.");

        ConcurrentLinkedQueue<UserInterfaceSession> sessionPool = new ConcurrentLinkedQueue<UserInterfaceSession>();

        AtomicInteger desiredCalls = new AtomicInteger(200);
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService es = Executors.newFixedThreadPool(numWorkerThreads);

        Map<String, String> formMap = new HashMap<>();
        formMap.put("body", "not important");
        formMap.put("subject", "test subject");
        formMap.put("replyTo", "no-reply@sailpoint.com");
        formMap.put("id", "not impotant");
        formMap.put("templateName", "not important");

        for (int i=0;i<=numWorkerThreads;i++) {
            es.submit(() -> {

                int threadCount = 0;

                while (callCount.get() < desiredCalls.get()) {

                    UserInterfaceSession uiSession = sessionPool.poll();
                    if (null == uiSession) {
                        log.info("Thread " + Thread.currentThread().getName() + " found pool empty, starting new CC session...");
                        uiSession = (UserInterfaceSession) SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
                        long sessionStart = System.currentTimeMillis();
                        try {
                            uiSession.open();
                        } catch (IOException e) {
                            log.error("Failure establising new CC session", e);
                            return;
                        }
                        uiSession.getNewSessionToken();
                        uiSession.stronglyAuthenticate();
                        long sessionSetup = System.currentTimeMillis() - sessionStart;
                        log.info("Successfully authenticated to CC session in " + sessionSetup + " msecs, CCSESSIONID:" + uiSession.getUniqueId());
                    }

                    uiSession.checkTokenExpiration();
                    int thisCall = callCount.incrementAndGet();

                    String responseJson = uiSession.doApiPost("/cc/api/emailTemplate/sendTestEmail", formMap);
                    if ((null != responseJson) && (responseJson.contains("email"))) {
                        log.info("call:" + thisCall + " Returned response: " + responseJson);
                    } else {
                        failureCount.incrementAndGet();
                        log.info("call:" + thisCall + " FAILED to return data; got back: " + responseJson);
                    }

                    threadCount++;
                    sessionPool.add(uiSession);

                }

                log.info("Thread exited @ theadCount:" + threadCount + "  callCount:" + callCount.get() + " failureCount:" + failureCount.get());
                es.shutdown();
            });

        }


        try {
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Failure awaiting thread pool termination" , e);
        }

        log.info("Worker Threads:" + numWorkerThreads + " Desired Calls:" + desiredCalls.get() + "  Total calls: " + callCount.get() + " failure count: " + failureCount.get());


    }
}
