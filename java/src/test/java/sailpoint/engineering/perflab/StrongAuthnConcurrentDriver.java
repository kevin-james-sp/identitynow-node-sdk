package sailpoint.engineering.perflab;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

public class StrongAuthnConcurrentDriver {
	
	public final static Logger log = LogManager.getLogger(StrongAuthnConcurrentDriver.class);

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		int numWorkerThreads = Integer.parseInt(System.getProperty("numWorkerThreads", "32"));
		
		log.info("Making Bulk strong authentication calls into " + envCreds.getOrgName() + " using " + numWorkerThreads + " threads.");
		
		AtomicInteger desiredCalls = new AtomicInteger(10000);
		AtomicInteger callCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);
		
		ExecutorService es = Executors.newFixedThreadPool(numWorkerThreads);
		
		for (int i=0;i<=numWorkerThreads;i++) {
			es.submit(() -> {
				
				int threadCount = 0;
				
				while (callCount.get() < desiredCalls.get()) {
						
					long sessionStart = System.currentTimeMillis();
					String ccSession = null;
					
					int thisCall = callCount.incrementAndGet();
					// Auto-close the uISession after every try() block.
					try (UserInterfaceSession uiSession = (UserInterfaceSession) SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC)) {
						UserInterfaceSession openOk = uiSession.open();
						if (null == openOk) {
							int failCount = failureCount.incrementAndGet();
							log.error("Failure establising new CC session, failure ratio: " + failCount + "/" + callCount.get());
						} else {
							ccSession = openOk.getUniqueId();
						}
						if (Boolean.parseBoolean(System.getProperty("skipUiSessionCall", "false"))) {
							log.debug("Skipping stronglyAuthenticate() due to skipUiSessionCall setting.");
						} else {
							// uiSession.getNewSessionToken();
							String newSession = uiSession.stronglyAuthenticate();
							if (null == newSession) {
								int failCount = failureCount.incrementAndGet();
								log.error("Failure establising strongly authenticated CC session, failure ratio: " + failCount + "/" + callCount.get());
							} else {
								ccSession = uiSession.getUniqueId();
							}
						}
						
					} catch (IOException e) {
						int failCount = failureCount.incrementAndGet();
						log.error("Failure establising new CC session, failure ratio: " + failCount + "/" + callCount.get(), e);
						return;
					}
					
					if (null != ccSession) {
						long sessionSetup = System.currentTimeMillis() - sessionStart;
						log.info("Call " + thisCall + " successfully authenticated to CC session in " + sessionSetup + " msecs, CCSESSIONID:" + ccSession);
					}
					
					threadCount++;
				
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
