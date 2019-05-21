package sailpoint.services.idn.sdk;

import retrofit2.Call;
import sailpoint.services.idn.sdk.services.*;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.io.IOException;

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
    public IdentityNowService( String url, String username, String password, String apiUser, String apiKey ) {
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
	public SessionBase createSession() throws IOException {
		return createSession(SessionType.SESSION_TYPE_API_ONLY);
	}

	public SessionBase createSession(SessionType sessionType) throws IOException {
		return createSession(sessionType, false);
	}

	public SessionBase createSession(SessionType sessionType, boolean stronglyAuthenticate) throws IOException {
		this.session = SessionFactory.createSession(this.creds, sessionType);
		this.session.open();
		if (stronglyAuthenticate) {
			((UserInterfaceSession)this.session).stronglyAuthenticate();
		}
		return this.session;
	}


  
	public <S> S getService ( Class<S> serviceClass ) throws IOException {
		return getService(serviceClass, ServiceTypes.UI);
	}

	public <S> S getService ( Class<S> serviceClass, ServiceTypes serviceTypes) throws IOException {
		if ( session == null )
			createSession();

		String url = serviceTypes == ServiceTypes.UI ? this.creds.getUserIntUrl() : this.creds.getGatewayUrl();
		return ServiceFactory.getService( serviceClass, url, this.session.getAccessToken());
	}
	
	/*
	 * Services
	 */
	public ConnectorService getConnectorService() throws IOException {
		return getService( ConnectorService.class );
	}
	
	public IdentityService getIdentityService() throws IOException {
		return getService( IdentityService.class );
	}
	
	public ReportService getReportService() throws IOException {
		return getService( ReportService.class );
	}
	
	public TransformService getTransformService() throws IOException {
		return getService( TransformService.class );
	}

	public RoleService getRoleService() throws IOException {
		return getService(RoleService.class, ServiceTypes.GATEWAY);
	}

	public SourceService getSourceService() throws IOException {
		return getService(SourceService.class, ServiceTypes.GATEWAY);
	}

	public EntitlementService getEntitlementService() throws IOException {
		return getService(EntitlementService.class, ServiceTypes.GATEWAY);
	}

	public AccountService getAccountService () throws IOException {
		return getService(AccountService.class, ServiceTypes.GATEWAY);
	}

    public AccessRequestService getAccessRequestService () throws IOException {
        return getService(AccessRequestService.class, ServiceTypes.GATEWAY);
    }

    public AccessProfileService getAccessProfileService () throws IOException {
        return getService(AccessProfileService.class, ServiceTypes.GATEWAY);
    }

    public SearchService getSearchService () throws IOException {
        return getService(SearchService.class, ServiceTypes.GATEWAY);
    }
  
	public static <T> T execute ( Call<T> call ) throws IOException {
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