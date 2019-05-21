package sailpoint.services.idn.sdk.scaffolding;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.Identity;
import sailpoint.services.idn.sdk.services.SearchService;
import sailpoint.services.idn.session.SessionType;

/**
 * Scaffolding infrastructure to support accessing the Search service.
 * @author adam.hampton
 *
 */
public class SearchScaffolding {

	public static void main(String[] args) {

		Log4jUtils.boostrapLog4j(Level.DEBUG);

		try {
			IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
			ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);
			SearchService searchService = ids.getSearchService();

			List<Identity> idList = searchService.searchIdentities(50, 0, "id=99999").execute().body();

		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
