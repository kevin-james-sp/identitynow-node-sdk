package sailpoint.services.idn.sdk;

import static org.junit.Assert.fail;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.junit.Test;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.internal.FrequentlyUsedCredentials;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.services.IdentityService;
import sailpoint.services.idn.session.SessionBase;

class AuthenticationTest {

	@Test
	void test() {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		List<ClientCredentials> credsList = FrequentlyUsedCredentials.getInstance().getAllTestCredentials();
		
		for (ClientCredentials creds : credsList) {
			
			IdentityNowService idNow = new IdentityNowService(creds);

			// TODO: Left off here figure out why this causes exceptions at the service level.
			SessionBase session;
			try {
				session = idNow.createSession();
				session.open();
				IdentityService idSvc = idNow.getIdentityService();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
		// Iterate through all the frequently used orgs and establish a session with them.
		fail("Not yet implemented");
	}

}
