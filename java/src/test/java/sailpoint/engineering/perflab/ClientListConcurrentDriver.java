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

public class ClientListConcurrentDriver {
	
	public final static Logger log = LogManager.getLogger(ClientListConcurrentDriver.class);

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		int numWorkerThreads = 12;
		log.info("Making Bulk api/client/list calls into " + envCreds.getOrgName() + " using " + numWorkerThreads + " threads.");
		
		// String clusterId = "275"; // For org dev01-useast1.cloud.sailpoint.com/perflab-05191440
		String clusterId = "289"; // For org dev01-useast1.cloud.sailpoint.com/perflab-09072140
		// String clusterId = "2057"; // For echo/diego-test.
		
		// Maintain a pool of already authenticated clients for the worker threads to use.
		// A worker thread initializes a client if the pool is empty when the thread starts
		// and shares it with the pool when it is done using the client pool.
		ConcurrentLinkedQueue<UserInterfaceSession> sessionPool = new ConcurrentLinkedQueue<UserInterfaceSession>();
		
		AtomicInteger desiredCalls = new AtomicInteger(1000000);
		AtomicInteger callCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);
		
		ExecutorService es = Executors.newFixedThreadPool(numWorkerThreads);
		
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
					
					long nanoStamp = System.nanoTime();
					String disableCacheString = nanoStamp + "." + Thread.currentThread().getId();
					
					String responseJson = uiSession.doApiGet("/cc/api/client/list?clusterId=" + clusterId + "&_dc=" + disableCacheString);
					if ((null != responseJson) && (responseJson.length() > 20)) {
						log.debug("call:" + thisCall + " " + responseJson.substring(0, 10) + "..." + responseJson.substring(responseJson.length()-10));
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
