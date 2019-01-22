package sailpoint.engineering.perflab;

import okhttp3.ResponseBody;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRequest;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRevoke;
import sailpoint.services.idn.sdk.object.accessrequest.RequestableObject;
import sailpoint.services.idn.sdk.services.AccessRequestService;
import sailpoint.services.idn.session.SessionType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RatsConcurrentDriver {

    private final static Logger log = LogManager.getLogger(RatsConcurrentDriver.class);

    // Parameters for requesting and resetting roles
    private final static String ROLE_NAME_TO_REQUEST = "Test role";
    private final static int NUMBER_OF_IDENTITIES_TO_REQUEST = 100;
    private final static String ROLE_NAME_TO_RESET = "Role Reset";

    // Parameters for querying user & role load test
    private final static int THREAD_COUNT = 10;
    private final static int REQUEST_COUNT = 100;


    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        //Request or revoke. Since these two are asynchronous, should not run them together.

        //requestRolesByRoleNameAndNumberOfIdentities();
        //revokeRolesByNumberOfIdentities();



        //Perf test the role or identities querying end point
        //queryRequestableObjectLoadTest(true, false);
    }

    /**
     * Request a given role on behalf of a given number of identities.
     * The identities are selected by name at alphabetic order until reaching the requested amount.
     * Calling this multiple times without revoking the role might end up using all the available identities for this role and this method will fail.
     * It's recommended to revoke the same amount of roles after running this method every time.
     */
    private static void requestRolesByRoleNameAndNumberOfIdentities() {
        try {
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccessRequestService accessRequestService = ids.getAccessRequestService();

            // Get role by name. Return error if not found
            List<RequestableObject> allRequestableList = accessRequestService.getRequestableObjects("250", "0", "me", "ROLE", "name").execute().body();
            RequestableObject role = allRequestableList.parallelStream().filter(roleObj -> roleObj.name.equals(ROLE_NAME_TO_REQUEST)).findFirst().orElse(null);
            if (role == null) {
                log.error("Role named \"" + ROLE_NAME_TO_REQUEST + "\" is not found. Exit.");
                return;
            }

            // Get the amount of identities to request. We can request for a maximum of 250 identities per request.
            List<String> identityList = new ArrayList<>();
            while (identityList.size() < NUMBER_OF_IDENTITIES_TO_REQUEST) {
                int identityToRequest = Math.min(NUMBER_OF_IDENTITIES_TO_REQUEST - identityList.size(), 250);
                List<RequestableObject> currentBatch = accessRequestService.getRequestableIdentities(role.id, Integer.toString(identityToRequest), Integer.toString(identityList.size()),
                        "name", "").execute().body();

                // Prevent infinite loop
                if (currentBatch == null || currentBatch.size() == 0) {
                    log.error("There are not enough available identities to complete this request. There are only " + Integer.toString(identityList.size()));
                    return;
                }

                identityList.addAll(currentBatch.parallelStream().map(RequestableObject::getId).collect(Collectors.toList()));
            }

            // Request this role on behalf of the identities
            Response<ResponseBody> res = accessRequestService.accessRequest(new AccessRequest(identityList, Collections.singletonList(role))).execute();
            if (!res.isSuccessful()) {
                String responseBody = res.body() == null ? "" : res.body().string();
                log.error("Failed while requesting for access. " + responseBody);
            }

            log.info("Done.");

        } catch (IOException e) {
            log.error("Cannot send request.", e);
        }
    }

    /**
     * Revoke role access on behalf of a given number of identities.
     * The identities are selected by name at alphabetic order until reaching the requested amount.
     * If you run the access request method many times before revoking, you need to adjust the NUMBER_OF_IDENTITIES_TO_REQUEST to
     * {$HowManyTimeYouRequested} * NUMBER_OF_IDENTITIES_TO_REQUEST
     */
    private static void revokeRolesByNumberOfIdentities() {
        try {
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);
            AccessRequestService accessRequestService = ids.getAccessRequestService();

            // Get the reset role by name. Return error if not found
            List<RequestableObject> allRequestableList = accessRequestService.getRequestableObjects("250", "0", "me", "ROLE", "name").execute().body();
            RequestableObject resetRole = allRequestableList.parallelStream().filter(roleObj -> roleObj.name.equals(ROLE_NAME_TO_RESET)).findFirst().orElse(null);
            if (resetRole == null) {
                log.error("Role named \"" + ROLE_NAME_TO_RESET + "\" is not found. Please create this as an empty role in the org and try again. Exit.");
                return;
            }

            // Get the role to revoke by name. Return error if not found
            RequestableObject role = allRequestableList.parallelStream().filter(roleObj -> roleObj.name.equals(ROLE_NAME_TO_REQUEST)).findFirst().orElse(null);
            if (role == null) {
                log.error("Role named \"" + ROLE_NAME_TO_REQUEST + "\" is not found. Failed to revoke identities from it. Exit.");
                return;
            }

            // Get the amount of identities to request. We can request for a maximum of 250 identities per request.
            List<String> identityList = new ArrayList<>();
            while (identityList.size() < NUMBER_OF_IDENTITIES_TO_REQUEST) {
                int identityToRequest = Math.min(NUMBER_OF_IDENTITIES_TO_REQUEST - identityList.size(), 250);
                List<RequestableObject> currentBatch = accessRequestService.getRequestableIdentities(resetRole.id, Integer.toString(identityToRequest), Integer.toString(identityList.size()),
                        "name", "").execute().body();

                // Prevent infinite loop
                if (currentBatch == null || currentBatch.size() == 0) {
                    log.error("There are not enough available identities to complete this request. There are only " + Integer.toString(identityList.size()));
                    return;
                }

                identityList.addAll(currentBatch.parallelStream().map(RequestableObject::getId).collect(Collectors.toList()));
            }

            // Revoke this role on behalf of the identity, one by one
            // TODO: We might be want some concurrency on this, not for perf test, but just to make this faster?
            identityList.forEach(identityId -> {
                try {
                    Response<Map<String, Object>> res = accessRequestService.accessRevoke(new AccessRevoke(role.id, identityId)).execute();
                    if (!res.isSuccessful()) {
                        String responseMap = res.body() == null ? "" : Arrays.toString(res.body().entrySet().toArray());
                        log.error("Failed to revoke role for " + identityId + ". " + responseMap);
                    }
                } catch (IOException e) {
                    log.error("Failed to revoke role for " + identityId + ". " + e.getMessage());
                }
            });

            log.info("Done.");

        } catch (IOException e) {
            log.error("Cannot send request.", e);
        }
    }

    /**
     * Load test API calls that retrieves available roles or available users
     * @param queryingRole Switch to toggle between querying roles or user.
     * @param applyFilter Switch to add filter string or not. This is only for querying users.
     */
    private static void queryRequestableObjectLoadTest(boolean queryingRole, boolean applyFilter) {
        try {
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccessRequestService accessRequestService = ids.getAccessRequestService();

            ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
            AtomicInteger requestCount = new AtomicInteger(0);
            Random random = new Random();

            // Select a random role in case we are querying available identities
            final RequestableObject role = accessRequestService.getRequestableObjects("250", "0", "me", "ROLE", "name").execute().body()
                        .stream().findAny().orElseThrow(IllegalStateException::new);

            // Run the multi threading test
            for (int i = 0; i <= THREAD_COUNT; i ++) {
                es.submit(() -> {
                    while (requestCount.getAndIncrement() <= REQUEST_COUNT) {

                        try {
                            String limit = Integer.toString(random.nextInt(250));
                            if (queryingRole) {
                                String offset = Integer.toString(random.nextInt(10));

                                //Execute and print result
                                long start = System.currentTimeMillis();
                                Response<List<RequestableObject>> res = accessRequestService.getRequestableObjects(limit, offset, "me", "ROLE", "name").execute();
                                long duration = System.currentTimeMillis() - start;

                                if (res.isSuccessful()) {
                                    log.info("Successfully got requestable roles with limit: " + String.format("%1$3s", limit) + " and offset: " + String.format("%1$2s", offset) + " in "
                                            + String.format("%1$6s", duration)  + " ms. Server returned object count: " + res.body().size());
                                } else {
                                    log.error("Failed to get requestable roles with limit: " + String.format("%1$3s", limit) + " and offset: " + String.format("%1$2s", offset) + " in "
                                            + String.format("%1$6s", duration) + " ms. Server returned error: " + res.errorBody().string());
                                }


                            } else {
                                String offset = Integer.toString(random.nextInt(1000));
                                String filterKeyWord =  Character.toString((char)(random.nextInt(26) + 'a'));
                                String filterString = applyFilter ? "name sw \"" + filterKeyWord + "\" or email sw \"" + filterKeyWord + "\"" : "";

                                //Execute and print result
                                long start = System.currentTimeMillis();
                                Response<List<RequestableObject>> res = accessRequestService.getRequestableIdentities(role.id, limit, offset, "name", filterString).execute();
                                long duration = System.currentTimeMillis() - start;

                                if (res.isSuccessful()) {
                                    log.info("Successfully got requestable identities with limit: " + String.format("%1$3s", limit) + " and offset: " + String.format("%1$4s", offset) +
                                            (applyFilter ? " and filter string " + filterKeyWord : "") + " in " + String.format("%1$6s", duration)  + " ms. Server returned object count: " + res.body().size());
                                } else {
                                    log.error("Failed to get requestable identities with limit: " + String.format("%1$3s", limit) + " and offset: " + String.format("%1$4s", offset) +
                                            (applyFilter ? " and filter string " + filterKeyWord : "")  + " in " + String.format("%1$6s", duration) + " ms. Server returned error: " + res.errorBody().string());
                                }
                            }
                        } catch (IOException e) {
                            log.error("Cannot send request.", e);
                        }
                    }
                    es.shutdown();
                });
            }

            // Wait until the tests are executed
            try {
                es.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                log.error("Failure awaiting thread pool termination" , e);
            }

            log.info("Done.");

        } catch (IOException e) {
            log.error("Cannot send request.", e);
        }


    }


}
