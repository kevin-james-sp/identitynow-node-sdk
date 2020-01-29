package sailpoint.services.idn.sdk.scaffolding;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.Identity;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

/**
 * Scaffolding infrastructure to support accessing the Search service.
 * @author adam.hampton
 *
 */
public class StepUpScaffolding {

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.DEBUG);
		
		try {
			
			IdentityNowService ids = new IdentityNowService(
					EnvironmentCredentialer.getEnvironmentCredentials()
			);
			System.out.println("Authenticating ...");
			UserInterfaceSession session = (UserInterfaceSession) ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);
			session.open();
			session.stronglyAuthenticate();
			System.out.println("getUniqueId: " + session.getUniqueId());
			System.out.println("Done!");
			
			// SearchService searchService = ids.getSearchService();
			// List<Identity> idList = searchService.searchIdentities(50, 0, "id=99999").execute().body();

		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
