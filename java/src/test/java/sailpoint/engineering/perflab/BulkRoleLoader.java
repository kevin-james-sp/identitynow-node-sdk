package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.accessprofile.AccessProfile;
import sailpoint.services.idn.sdk.object.role.Role;
import sailpoint.services.idn.sdk.services.AccessProfileService;
import sailpoint.services.idn.sdk.services.RoleService;

import java.io.IOException;

public class BulkRoleLoader {

	public final static Logger log = LogManager.getLogger(ClientListConcurrentDriver.class);
	private static final String TEST_USER_COUNT = System.getProperty("testRoleCount", "1000");
	//public static final String BULK_ROLE_SOURCE
	public static void main(String[] args){

		//Creds and logger
		Log4jUtils.boostrapLog4j(Level.INFO);

		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		IdentityNowService ids = new IdentityNowService(envCreds);

		try{
			//required service, and desired roles.
			RoleService _roleService = ids.getRoleService();
			AccessProfileService _apService = ids.getAccessProfileService();
			int userCount = Integer.parseInt(TEST_USER_COUNT);

			//Get required ids


			//Create required access profile.
			AccessProfile ap = new AccessProfile();
			ap.description = "BulkRoleLoaded Profile";
			//ap.entitlements = ListOfEntitlementsHere
			ap.name = "Bulk Role";
			//ap.ownerId = $supportIdHere
			//ap.sourceId = $sourceIdHere

			//Do the executions
			for(int i = 0; i < userCount; i++){

				//Create the role and update it
				Role role = new Role("BulkRole" + i, "Chandlery automation bulk role object.");
				role.disabled = false;
				role.owner = "support";
				role.requestable = true;
				role = _roleService.create(role).execute().body();

			}

		} catch(IOException e){
			log.error("An IOException has occurred when trying to get the role service.");
		}
	}
}
