package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.OkHttpClient;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

public class ClientListDriver {
	
	public final static Logger log = LogManager.getLogger(ClientListDriver.class);

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		// String clusterId = "275"; // For org dev01-useast1.cloud.sailpoint.com/perflab-05191440
		String clusterId = "289"; // For org dev01-useast1.cloud.sailpoint.com/perflab-09072140
		
		try (SessionBase session = SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC) ){
			UserInterfaceSession uiSession = (UserInterfaceSession) session; 
			uiSession.open();
			uiSession.getNewSessionToken();
			uiSession.stronglyAuthenticate();
			log.info("Successfully authenticated to API session: " + session.getUniqueId());
			
			for (int i=0;i<10000;i++) {
				String responseJson = uiSession.doApiGet("/cc/api/client/list?clusterId=" + clusterId + "&_dc=" + System.currentTimeMillis());
				if ((null != responseJson) && (responseJson.length() > 20)) {
					log.info("Call " + i + ": " + responseJson.substring(0, 10) + "..." + responseJson.substring(responseJson.length()-10));
				} else {
					log.info("Call " + i + ": FAILED to return data; got back: " + responseJson);
				}
				
				// Emulate the UI polling for user status.
				if ((i>0) && (0 == i%20)) {
					String userStatus = uiSession.doApiGet("/cc/api/user/status?_dc=" + System.currentTimeMillis());
				}
				
				// Refresh the JWT token every 100 calls or so.
				if ((i>0) && (0 == i%100)) uiSession.getNewSessionToken();
			}

		} catch (Exception e) {
			log.error("Failure while processing.", e);
		} 

	}

}
