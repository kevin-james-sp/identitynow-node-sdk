package sailpoint.services.idn.sdk;

import retrofit2.Call;
import sailpoint.services.idn.sdk.services.*;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

public final class IdentityNowService {
	
	/*
	 * Instance variables
	 */
	private ClientCredentials creds;
    private SessionBase session;
    
    /*
     * Constructors
     */
    
    /**
     * Construct an IdentityNow service using caller specified credentials.
     * @param url
     * @param username
     * @param password
     * @param apiUser
     * @param apiKey
     * @throws Exception
     */
    public IdentityNowService( String url, String username, String password, String apiUser, String apiKey ) throws Exception {
    	this.creds = new ClientCredentials();
    	
    	// String userIntUrl, String orgName, String orgUser, 
		// String orgPass, String clientId, String clientSecret
		
    	creds.setUserIntUrl(url);
    	creds.setOrgUser(username);
    	creds.setOrgPass(password);
    	creds.setClientId(apiUser);
    	creds.setClientSecret(apiKey);
		this.session = null;
	}
    
    /**
     * Construct an IdentityNow service using caller supplied ClientCredentials.
     * @param clientCredentials
     */
	public IdentityNowService( ClientCredentials clientCredentials) {
		this.creds = clientCredentials;
		this.session = null;
	}
	
	/**
     * Construct an IdentityNow service using environment supplied ClientCredentials.
     */
	public IdentityNowService() {
		this.creds = EnvironmentCredentialer.getEnvironmentCredentials();
		this.session = null;
	}
	
	/*
	 * Methods
	 */
	public SessionBase createSession() throws Exception {
		return createSession(SessionType.SESSION_TYPE_API_ONLY);
	}

	public SessionBase createSession(SessionType sessionType) throws Exception {
		return createSession(sessionType, false);
	}

	public SessionBase createSession(SessionType sessionType, boolean stronglyAuthenticate) throws Exception {
		this.session = SessionFactory.createSession(this.creds, sessionType);
		if (stronglyAuthenticate) {
			this.session.open();
			((UserInterfaceSession)this.session).stronglyAuthenticate();
		}
		return this.session;
	}


  
	public <S> S getService ( Class<S> serviceClass ) throws Exception {
		return getService(serviceClass, ServiceTypes.UI);
	}

	public <S> S getService ( Class<S> serviceClass, ServiceTypes serviceTypes) throws Exception {
		if ( session == null )
			createSession();

		String url = serviceTypes == ServiceTypes.UI ? this.creds.getUserIntUrl() : this.creds.getGatewayUrl();
		return ServiceFactory.getService( serviceClass, url, this.session.getAccessToken());
	}
	
	/*
	 * Services
	 */
	public ConnectorService getConnectorService() throws Exception {
		return getService( ConnectorService.class );
	}
	
	public IdentityService getIdentityService() throws Exception {
		return getService( IdentityService.class );
	}
	
	public ReportService getReportService() throws Exception {
		return getService( ReportService.class );
	}
	
	public TransformService getTransformService() throws Exception {
		return getService( TransformService.class );
	}

	public RoleService getRoleService() throws Exception {
		return getService(RoleService.class, ServiceTypes.GATEWAY);
	}

	public SourceService getSourceService() throws Exception {
		return getService(SourceService.class, ServiceTypes.GATEWAY);
	}

	public EntitlementService getEntitlementService() throws Exception {
		return getService(EntitlementService.class, ServiceTypes.GATEWAY);
	}
  
	public static <T> T execute ( Call<T> call ) throws Exception {	
		return call.execute().body();
	}

	/*
	 * Service types
	 */
	enum ServiceTypes {
		UI,
		GATEWAY
	}
  
}