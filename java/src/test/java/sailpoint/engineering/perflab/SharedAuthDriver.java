package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.concurrent.threads.SessionExecutorThread;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.internal.FeatureFlagService;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.session.SessionType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SharedAuthDriver {

	public final static Logger log = LogManager.getLogger(SharedAuthDriver.class);

	//Currently this test is expected to run against perflab-05101139 (other orgs must load the HR Performance source in order for this test to work there)
	public static void main(String[] args){
		//Vars
		int successfulLogins = 0;
		int successfulAuthLogins = 0;
		long executionTime = 0;
		long startTime = 0;
		long sharedAuthExecutionTime = 0;
		int numSessions = args.length >= 2 ? Integer.valueOf(args[0]) : 1;
		int numThreads = args.length >= 2 ? Integer.valueOf(args[1]) : 1;
		boolean testSharedAuthOnly = args.length == 3 ? Boolean.parseBoolean(args[2]) : true;
		Log4jUtils.boostrapLog4j(Level.INFO);

		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		IdentityNowService ids = new IdentityNowService(envCreds);
		FeatureFlagService _ffService = new FeatureFlagService(null);
		LinkedList<SessionExecutorThread> workQueue = new LinkedList<>();

		try {
			ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, false);
		} catch (IOException e) {
			log.error("Unable to get session.", e);
		}

		EnvironmentCredentialer environmentCredentialer = new EnvironmentCredentialer();
		//ListIterator<SessionExecutorThread> iter = workQueue.listIterator();

		//Load work queue with threads
		for (int i = 0; i < numSessions; i++) {
			workQueue.push(new SessionExecutorThread(environmentCredentialer.getEnvironmentCredentials(), "10000" + Integer.toString(i)));
		}

		if(!testSharedAuthOnly) {
			log.info("======================================================================================================");
			log.info(" ");
			log.info("Testing OpenAM performance on org: " + envCreds.getOrgName() + " with user: " + envCreds.getOrgUser());
			log.info(" ");
			log.info("======================================================================================================");

			//Set flag to non shared auth service for comparison
			_ffService.setFlagForOrg(false, FeatureFlagService.FEATURE_FLAGS.SHARED_AUTH_PTA);

			//Time and execute
			startTime = System.currentTimeMillis();
			successfulLogins = executeLogins(workQueue, numThreads);
			executionTime = System.currentTimeMillis() - startTime;

			log.info("======================================================================================================");
			log.info(" ");
			log.info("OpenAM test complete.");
			log.info(" ");
			log.info("======================================================================================================");

			//Enable auth service
			_ffService.setFlagForOrg(true, FeatureFlagService.FEATURE_FLAGS.SHARED_AUTH_PTA);
		}

		log.info("======================================================================================================");
		log.info(" ");
		log.info("Now testing the Shared Auth Login Service...");
		log.info(" ");
		log.info("======================================================================================================");

		//Time and execute
		startTime = System.currentTimeMillis();
		successfulAuthLogins = executeLogins(workQueue, numThreads);
		sharedAuthExecutionTime = System.currentTimeMillis() - startTime;

		log.info("======================================================================================================");
		log.info(" ");
		log.info("TEST RESULTS: ");
		log.info("Org: " + envCreds.getOrgName());
		log.info("User: " + envCreds.getOrgUser());
		if(!testSharedAuthOnly){
			log.info("Baseline: ");
			log.info("Successful logins: " + successfulLogins);
			log.info("Time in milliseconds: " + executionTime);
			log.info("");
		}
		log.info("Shared Auth: ");
		log.info("Successful logins: " + successfulAuthLogins);
		log.info("Time in milliseconds: " + sharedAuthExecutionTime);
		if(!testSharedAuthOnly)
			log.info("Results: " + "Successful OpenAM: " + successfulLogins + " Successful Auth: " + successfulAuthLogins + " % change: " + getPercentChange(executionTime, sharedAuthExecutionTime));
		else
			log.info("Results: " + " Successful Auth: " + successfulAuthLogins);
		log.info(" ");
		log.info("======================================================================================================");
	}

	private static int executeLogins(List<SessionExecutorThread> workQueue, int numThreads){
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		List<Future<Boolean>> resultList = null;

		//Start get start time, and execute.
		try{
			resultList = executor.invokeAll(workQueue);
			executor.shutdown();
		}
		catch(InterruptedException e){
			log.error("Executor failed to invoke all login threads due to an InterruptedException.", e);
			System.exit(1);
		}

		//Process successes
		try{
			return processAuthResultList(resultList);
		}
		catch(InterruptedException e){
			log.error("A Future object failed to return a value due to an InterruptedException.", e);
			System.exit(1);
			return -1;
		}
		catch (ExecutionException f){
			log.error("A Future object failed to return a value due to an ExecutionException.", f);
			System.exit(1);
			return -1;
		}
		catch(NullPointerException g){
			log.error("A Null Pointer Exception has occurred. The executor did not return a value for it's resultList variable.", g);
			System.exit(1);
			return -1;
		}
	}

	//Helper method for executeLogins
	private static int processAuthResultList(List<Future<Boolean>> resultList) throws InterruptedException, ExecutionException, NullPointerException {
		//count successful logins, and throw null pointer if resultList is null.
		if(resultList == null)
			throw new NullPointerException();
		int successfulLogins = 0;
		for(Future<Boolean> result : resultList)
			if(result.isDone() && result.get())
				successfulLogins++;

		return successfulLogins;
	}

	public static double getPercentChange(double original, double change){
		return ((change - original) / original) * 100;
	}
}
