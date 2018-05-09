package sailpoint.services.idn.session;

import java.io.IOException;

import sailpoint.services.idn.sdk.ClientCredentials;

/**
 * The top level class from which all other Session types derive. 
 * 
 * @author adam.hampton
 *
 */
public class SessionBase implements java.lang.AutoCloseable {
	
	protected ClientCredentials creds = null;
	
	// Default to API Only session types for SDK use.
	protected SessionType sessionType = SessionType.SESSION_TYPE_API_ONLY;
	
	protected String accessToken = null;
	protected int expiresIn = -1;
	protected boolean isAthenticated = false;
	
	public SessionBase (ClientCredentials clientCredentials) {
		if (null == clientCredentials.getOrgName()) {
			throw new IllegalArgumentException("ClientCredentials must contain an Organization Name to construct a Session.");
		}
		this.creds = clientCredentials;
	}
	
	/**
	 * Connect to the IdentityNow service and establish the session.  This "logs in"
	 * using whatever means the session has at its disposal to connect to the service.
	 * 
	 * Returns a self-reference for chain-able operations.
	 */
	public SessionBase open() throws IOException {
		throw new IllegalArgumentException("Session sub-classes must implement their own open() methods.");
	}
	
	/**
	 * Return a new unique ID for the connected session; depends on session type being established. 
	 * @return
	 */
	public String getUniqueId() {
		throw new IllegalArgumentException("Session sub-classes must implement their own getUniqueId() methods.");
	}

	public SessionType getSessionType() {
		return sessionType;
	}

	public void setSessionType(SessionType sessionType) {
		this.sessionType = sessionType;
	}

//	public ClientCredentials getCreds() {
//		return creds;
//	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	@Override
	public void close() throws Exception {
		throw new IllegalArgumentException("Session sub-classes must implement their own close() methods.");
	}
	
	public boolean isAuthenticated() {
		return isAthenticated;
	}
	
	protected ClientCredentials getCredentials() {
		return creds;
	}
	
	public String getApiGatewayUrl() {
		return creds.getGatewayUrl();
	}
	
	public String getUserInterfaceUrl() {
		return creds.getUserIntUrl();
	}

}
