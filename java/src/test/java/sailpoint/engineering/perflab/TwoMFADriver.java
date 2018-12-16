package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.concurrent.threads.TwoMfaThread;
import sailpoint.services.idn.console.Log4jUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TwoMFADriver {

    private final static Logger log = LogManager.getLogger(TwoMFADriver.class);

    private static final String PERF_DEFAULT_PWD = "p@sSw04d!4AD4me-001";

    // Example JVM & Jenkins options:
    // -DccDbPassword=thePassword -DtestUserCount=10000 -DtestThreadCount=20 -Dgoal=accnt-unlock -DmfaKba=true -DmfaEmail=true
    private static final String CC_DB_PASSWORD = System.getProperty("ccDbPassword");
    private static final String TEST_USER_COUNT = System.getProperty("testUserCount", "1000");
    private static final String TEST_THREAD_COUNT = System.getProperty("testThreadCount", "20");
    private static final String GOAL = System.getProperty("goal", "pswd-reset");
    private static final String MFA_KBA = System.getProperty("mfaKba", "true");
    private static final String MFA_EMAIL = System.getProperty("mfaEmail", "true");
    private static final String MFA_SMS = System.getProperty("mfaSms", "false");

    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        //Validate test configuration
        if (CC_DB_PASSWORD == null) {
            log.error("Failed to start 2MFA load test. Please provide CC database password through system properties (JVM options)");
        } else {
            try {
                int userCount = Integer.parseInt(TEST_USER_COUNT);
                int threadCount = Integer.parseInt(TEST_THREAD_COUNT);

                ExecutorService executor = Executors.newFixedThreadPool(threadCount);
                List<TwoMfaThread> workQueue = new LinkedList<>();
                int successfulResets;
                String username;
                long startTime;
                long duration;

                if (userCount > 0 && threadCount > 0) {
                    if(userCount > 9000){
                        log.warn("Currently, only 9000 users are available for this test, defaulting to 9000");
                        userCount = 10000;
                    }
                    //Validation complete. Print start message and start
                    log.info("Starting 2MFA load test with " + TEST_USER_COUNT + " users and " + TEST_THREAD_COUNT + " threads.");

                    //Build work queue, and execute
                    for(int i = 1000 ; i < userCount ; i++){
                        username = Integer.toString(i);
                        workQueue.add(new TwoMfaThread(GOAL, username, MFA_KBA, MFA_EMAIL, MFA_SMS, CC_DB_PASSWORD, PERF_DEFAULT_PWD));
                    }
                    startTime = System.currentTimeMillis();
                    successfulResets = processResultList( executor.invokeAll(workQueue));
                    duration = System.currentTimeMillis() - startTime;

                    //Print results
                    log.info("TEST RESULTS: ");
                    log.info("Successful resets: " + successfulResets);
                    log.info("Time in milliseconds: " + duration);
                    log.info("");
                    log.info("Results: " + "Successful Resets: " + successfulResets);

                } else {
                    log.error("Failed to start 2MFA load test. User and thread count must be POSITIVE integers");
                }

            } catch (NumberFormatException e) {
                log.error("Failed to start 2MFA load test. User and thread count must be integers");
            } catch (InterruptedException e){
                log.error("The executor has encountered an InterruptedException");
            } catch (ExecutionException e){
                log.error("An ExecutionException has occurred when processing the list of results.");
            } catch (NullPointerException e){
                log.error("A NullPointerException has occurred. The result list of results was null.");
            }
        }
    }

    private static int processResultList(List<Future<Boolean>> resultList) throws InterruptedException, ExecutionException, NullPointerException{
        //count successful logins, and throw null pointer if resultList is null.
        if(resultList == null)
            throw new NullPointerException();
        int successfulResets = 0;
        for(Future<Boolean> result : resultList)
            if(result.isDone() && result.get())
                successfulResets++;

        return successfulResets;
    }
}
