package sailpoint.services.idn.console;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

public class StrongAuthenticationValidator {

	public static void main(String[] args) {
		
		int exitVal = 0;
		
		System.out.println("");
		System.out.println("IdentityNow Services SDK - Strong Authentication Utility.");
		System.out.println("Copyright (C) 2018, SailPoint Technologies, Inc.");
		System.out.println("All Rights Reserved.");
		System.out.println("");
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		if (envCreds.hasUserCredentials()) {
			System.out.println("Attempting to login to " + envCreds.getOrgName() + " UI as " + envCreds.getOrgUser() + " ...");
			try (UserInterfaceSession uiSession = (UserInterfaceSession) SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC) ){
				uiSession.open();
				System.out.println("Successfully authenticated to UI session. CCSESSIONID: " + uiSession.getUniqueId());
				System.out.println("Strongly authenticating UI session ...");
				uiSession.getNewSessionToken();
				String jwtToken = uiSession.stronglyAuthenticate();
				if (null != jwtToken) {
					System.out.println("Strongly authenticated to UI, token: " + jwtToken);
				} else {
					System.out.println("Failed to strongly authenticate.  Validate enviroment credentials; Is KBA enalbed and are the answers correct?");
					exitVal = 1;
				}
			} catch (Exception e) {
				System.out.println("Failed to esablish UI type session to the org.");
				e.printStackTrace();
				exitVal = 1;
			} 
		} else {
			System.err.println("No user credentials present in environment configuration!");
			exitVal = 1;
		}
		
		System.exit(exitVal);
	}

}
