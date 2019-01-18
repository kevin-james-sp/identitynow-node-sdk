package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRequest;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRequestResponse;
import sailpoint.services.idn.sdk.object.accessrequest.RequestableObject;
import sailpoint.services.idn.sdk.services.AccessRequestService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class RatsConcurrentDriver {

    private final static Logger log = LogManager.getLogger(RatsConcurrentDriver.class);

    public static void main(String[] args) {
        Log4jUtils.boostrapLog4j(Level.INFO);

        try {
            IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
            UserInterfaceSession uiSession = (UserInterfaceSession)ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
            AccessRequestService accessRequestService = ids.getAccessRequestService();

            List<RequestableObject> allRequestableList = accessRequestService.getRequestableObjects("50", "0", "me", "ROLE", "name").execute().body();

            long start = System.currentTimeMillis();
            List<RequestableObject> requestIdentitiesList = accessRequestService.getRequestableIdentities(allRequestableList.get(0).id, "2", "2", "name", "").execute().body();
            long internal = System.currentTimeMillis() - start;

//            String error = requestIdentitiesList.errorBody().string();
//
            List<RequestableObject> availableRequestableList = allRequestableList.parallelStream().filter(obj -> obj.requestStatus.equals("AVAILABLE")).collect(Collectors.toList());

            AccessRequest accessRequest = new AccessRequest(Arrays.asList("2c91808565822c1601658b37775404a3"), availableRequestableList);

            AccessRequestResponse accessRequestResponse = accessRequestService.accessRequest(accessRequest).execute().body(); //Check if error is null



            System.out.println(1);


        } catch (IOException e) {
            log.error("Cannot send request.", e);
        }

    }
}
