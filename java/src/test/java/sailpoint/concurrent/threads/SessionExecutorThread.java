package sailpoint.concurrent.threads;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.session.SessionType;

import java.util.concurrent.Callable;

public class SessionExecutorThread implements Callable<Boolean> {
	ClientCredentials envCreds;

	public SessionExecutorThread(){
	}

	public SessionExecutorThread(ClientCredentials envCreds){
		this.envCreds = envCreds;
	}

	@Override
	public Boolean call() throws Exception {
		Thread thisThread = Thread.currentThread();
		String thisName = thisThread.getName();

		if(envCreds == null)
			envCreds = EnvironmentCredentialer.getEnvironmentCredentials();

		System.out.println(thisName + " attempting to login to " + envCreds.getOrgName() + " UI as " + envCreds.getOrgUser() + " ...");
		// Note: we don't want a try-with-resources here b/c we're returning the UI session back to the caller.
		try {
			ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
			IdentityNowService ids = new IdentityNowService(envCreds);
			ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, false);

			System.out.println(thisName + " successfully authenticated to UI session.");
			return true;
		} catch (Exception e) {
			System.out.println(thisName + " failed to esablish UI type session to the org.");
			e.printStackTrace();
			return false;
		}
	}
}