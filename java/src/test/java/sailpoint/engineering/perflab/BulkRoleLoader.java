package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.accessprofile.AccessProfile;
import sailpoint.services.idn.sdk.object.role.ComplexRoleCriterion;
import sailpoint.services.idn.sdk.object.role.Role;
import sailpoint.services.idn.sdk.object.role.RoleCriterion;
import sailpoint.services.idn.sdk.object.role.RoleCriterionKey;
import sailpoint.services.idn.sdk.object.role.Selector;
import sailpoint.services.idn.sdk.services.AccessProfileService;
import sailpoint.services.idn.sdk.services.RoleService;
import sailpoint.services.idn.session.SessionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BulkRoleLoader {

	public final static Logger log = LogManager.getLogger(BulkRoleLoader.class);
	private static final String TEST_USER_COUNT = System.getProperty("testRoleCount", "100");
	//public static final String BULK_ROLE_SOURCE
	public static void main(String[] args) throws IOException{

		//Creds and logger
		Log4jUtils.boostrapLog4j(Level.ALL);

		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		IdentityNowService ids = new IdentityNowService(envCreds);
		ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);
		log.info("Session created");

		try{

			log.info("Inside the try block.");
			//required service, and desired roles.
			RoleService _roleService = ids.getRoleService();
			AccessProfileService _apService = ids.getAccessProfileService();
			int userCount = Integer.parseInt(TEST_USER_COUNT);

			log.info("Should have all of the services now.");

			//Get required ids


			//Create required access profile.
			AccessProfile ap = new AccessProfile();
			ap.description = "BulkRoleLoaded Profile";
			//ap.entitlements = ListOfEntitlementsHere
			ap.name = "Bulk Role";
			//ap.ownerId = $supportIdHere
			//ap.sourceId = $sourceIdHere

			RoleCriterionKey key = new RoleCriterionKey("IDENTITY","attribute.uid","");
			RoleCriterion criterion = new RoleCriterion("CONTAINS",key, "260");
			List<RoleCriterion> criterionList = new ArrayList<>();
			criterionList.add(criterion);
			ComplexRoleCriterion roleCriterion = new ComplexRoleCriterion("OR",criterionList);
			Selector selector = new Selector("COMPLEX_CRITERIA", roleCriterion);

			//Do the executions
			for(int i = 0; i < userCount; i++){

				//Create the role and update it
				Role role = new Role("BulkRole" + i, "Chandlery automation bulk role object.");
				role.disabled = false;
				role.owner = "support";
				role.requestable = true;
				role.selector = selector;

				Response<Role> roleResponse = _roleService.create(role).execute();

			}

		} catch(IOException e){
			log.error("An IOException has occurred when trying to get the role service.");
		}
	}
}
