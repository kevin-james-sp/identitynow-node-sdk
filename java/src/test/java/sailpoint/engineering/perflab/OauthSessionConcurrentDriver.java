package sailpoint.engineering.perflab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LongSummaryStatistics;
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

public class OauthSessionConcurrentDriver {
	
	public static Logger log = null;

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		// Assign this _after_ bootstrapping log4j to prevent errors in output.
		log = LogManager.getLogger(OauthSessionConcurrentDriver.class);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		int numWorkerThreads = Integer.parseInt(System.getProperty("numWorkerThreads", "10"));
		
		AtomicInteger numUiSessionCalls = new AtomicInteger(Integer.parseInt(System.getProperty("numUiSessionCalls", "1000000")));
		
		log.info("Making " + numUiSessionCalls +" /ui/session calls on " + envCreds.getOrgName() + " using " + numWorkerThreads + " threads.");
		
		AtomicInteger interThreadStartupDelay = new AtomicInteger(Integer.parseInt(System.getProperty("interThreadStartupDelay", "2500")));
		
		AtomicInteger desiredLoginCalls = new AtomicInteger(Integer.parseInt(System.getProperty("loginCallsPerThread", "10")));
		AtomicInteger loginCallCount = new AtomicInteger(0);
		AtomicInteger uiSessionCallCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);
		
		AtomicInteger activeThreadCount = new AtomicInteger(0);
		
		ExecutorService es = Executors.newFixedThreadPool(numWorkerThreads);
		
		AtomicInteger threadStarupDelaySerial = new AtomicInteger(0);
		
		// Note: see the while() conditions below.  We keep creating a new worker thread
		// every 1/2 second while the worker thread activeThreadCount is less than the
		// numWorkerThreads limit.  This way we re-spawn worker threads that have login
		// failures the way that new users would try to 
		do {
			
			// Spawn a new worker thread.
			if (activeThreadCount.get() < numWorkerThreads) {
				es.submit(() -> {
					
					int activeId = activeThreadCount.incrementAndGet();
					log.info("Launching new worker thread, active count:" + activeId);
					
					int threadCount = 0;
					
					/* Start-up delay handled by the do/while loop outside.
					try {
						Thread.sleep(25 * threadStarupDelaySerial.incrementAndGet());
					} catch (Exception ex) {
						log.error("Failure while waiting for thread start up delay.", ex);
					}
					*/
					
					while (uiSessionCallCount.get() < numUiSessionCalls.get()) {
							
						long sessionStart = System.currentTimeMillis();
						String ccSession = null;
						
						int thisLoginCall= loginCallCount.incrementAndGet();
						
						// Auto-close the uISession after every try() block.
						try (UserInterfaceSession uiSession = (UserInterfaceSession) SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC)) {
							
							UserInterfaceSession openOk = uiSession.open();
							if (null == openOk) {
								int failCount = failureCount.incrementAndGet();
								log.error("Failure establising new CC session, failure ratio: " + failCount + "/" + loginCallCount.get());
								activeThreadCount.decrementAndGet();
								return;
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
									log.error("Failure establising strongly authenticated CC session, failure ratio: " + failCount + "/" + loginCallCount.get());
									activeThreadCount.decrementAndGet();
									return;
								} else {
									ccSession = uiSession.getUniqueId();
								}
								
								LongSummaryStatistics localStats = new LongSummaryStatistics();
								
								for (int uiSessionCalls=0; uiSessionCalls<numUiSessionCalls.get(); uiSessionCalls++) {
									
									long startTime = System.currentTimeMillis();
									String multiCallStr = uiSession.getNewSessionToken();
									long duration = System.currentTimeMillis() - startTime;
									
									// Handle a non-200 response here.
									if (null == multiCallStr) {
										log.error("Got a non-200 response, shutting down this thread.");
										break; // out of for() loop.
									}
								
									localStats.accept(duration);
									
									// Warn if the duration takes a long time, give it an irrationally long time.
									if (duration > 25000) {
										log.warn("Long call to /ui/session - duration: " + duration + " msecs for session:" + multiCallStr);
									}
									
									int thisGlobalCall = uiSessionCallCount.incrementAndGet();
									
									if ((uiSessionCalls % 5) == 0) {
										String appList = uiSession.doApiGet("/cc/api/app/list");
										log.debug("cc/api/app/list:" + appList);
									}
									
									if ((uiSessionCalls % 3) == 0) {
										String appList = uiSession.doApiGet("/cc/api/user/status");
										log.debug("cc/api/user/status:" + appList);
									}
									
									if ((uiSessionCalls % 50) == 0) {
										String statsStr = String.format("%d/%d/%f", localStats.getMin(), localStats.getMax(), localStats.getAverage());
										log.info("Completed local " + uiSessionCalls + ", global:" + thisGlobalCall + " stats m/x/a:" + statsStr);
										localStats = new LongSummaryStatistics();
									}
									
								}
								
							}
							
						} catch (IOException e) {
							int failCount = failureCount.incrementAndGet();
							log.error("Failure establising new CC session, failure ratio: " + failCount + "/" + loginCallCount.get(), e);
							activeThreadCount.decrementAndGet();
							return;
						}
						
						if (null != ccSession) {
							long sessionSetup = System.currentTimeMillis() - sessionStart;
							log.info("Call " + thisLoginCall + " successfully authenticated to CC session in " + sessionSetup + " msecs, CCSESSIONID:" + ccSession);
						}
						
						threadCount++;
					
					}
					
					log.info("Thread exited @ theadCount:" + threadCount + "  callCount:" + loginCallCount.get() + " failureCount:" + failureCount.get());
					if (uiSessionCallCount.get() >= numUiSessionCalls.get()) {
						es.shutdown();
					}
					activeThreadCount.decrementAndGet();
				});

			}

			try {
				Thread.sleep(interThreadStartupDelay.get());
			} catch (InterruptedException e) {
				log.error("Failedure while sleeping in polling loop.", e);
			}
		
		} while (
				(activeThreadCount.get() < numWorkerThreads) && 
				(uiSessionCallCount.get() < numUiSessionCalls.get())
		);
		
		try {
			es.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			log.error("Failure awaiting thread pool termination" , e);
		}
		
		log.info("Worker Threads:" + numWorkerThreads + " Desired Calls:" + desiredLoginCalls.get() + "  Total calls: " + loginCallCount.get() + " failure count: " + failureCount.get());
		
	}

}
