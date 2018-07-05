package sailpoint.services.idn.console;

import java.io.IOException;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionBase;
// import sailpoint.services.idn.sdk.object.Tenant;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;

public class CredentialsValidator {

	public static void main(String[] args) {
		
		int exitVal = 0;
		
		System.out.println("");
		System.out.println("IdentityNow Services SDK - Credentials Validation Utility.");
		System.out.println("Copyright (C) 2018, SailPoint Technologies, Inc.");
		System.out.println("All Rights Reserved.");
		System.out.println("");
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		/* This works fine. 
		if (envCreds.hasApiCredentials()) {
			try (SessionBase session = SessionFactory.createSession(SessionType.SESSION_TYPE_API_ONLY) ){
				session.open();
				System.out.println("Successfully authenticated to API session: " + session.getUniqueId());
			} catch (IOException e) {
				System.out.println("Failed to esablish API type session to the org.");
				e.printStackTrace();
				exitVal = 1;
			} catch (Exception e) {
				System.out.println("Failed to esablish API type session to the org.");
				e.printStackTrace();
				exitVal = 1;
			} 
		}
		*/
		
		if (envCreds.hasUserCredentials()) {
			try (SessionBase session = SessionFactory.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC) ){
				session.open();
				System.out.println("Successfully authenticated to UI session: " + session.getUniqueId());
			} catch (IOException e) {
				System.out.println("Failed to esablish UI type session to the org.");
				e.printStackTrace();
				exitVal = 1;
			} catch (Exception e) {
				System.out.println("Failed to esablish UI type session to the org.");
				e.printStackTrace();
				exitVal = 1;
			} 
		}
		
		System.exit(exitVal);

	}

}
