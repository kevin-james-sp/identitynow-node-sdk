package sailpoint.concurrent.threads;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

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
		UserInterfaceSession uiSession = null;
		// Note: we don't want a try-with-resources here b/c we're returning the UI session back to the caller.
		try {
			uiSession = (UserInterfaceSession) SessionFactory.createSession(envCreds, SessionType.SESSION_TYPE_UI_USER_BASIC);
			uiSession.open();
			System.out.println(thisName + " successfully authenticated to UI session. CCSESSIONID: " + uiSession.getUniqueId());
			return true;
		} catch (Exception e) {
			System.out.println(thisName + " failed to esablish UI type session to the org.");
			e.printStackTrace();
			return false;
		}
	}
}