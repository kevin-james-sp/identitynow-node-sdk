package sailpoint.engineering.perflab;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.session.SessionType;

import java.io.IOException;

public class JSONHackLab {

	public final static Logger log = LogManager.getLogger(JSONHackLab.class);

	public JSONHackLab() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		Log4jUtils.boostrapLog4j(Level.INFO);


		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		IdentityNowService ids = new IdentityNowService(envCreds);
		ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);
		System.out.print("Done");
		
		
		

	}

}
