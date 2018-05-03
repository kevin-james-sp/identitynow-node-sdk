package sailpoint.services.idn.sdk;

import retrofit2.Call;
import sailpoint.services.idn.sdk.services.ConnectorService;
import sailpoint.services.idn.sdk.services.IdentityService;
import sailpoint.services.idn.sdk.services.ReportService;
import sailpoint.services.idn.sdk.services.TransformService;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionFactory;
import sailpoint.services.idn.session.SessionType;

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
     * @param clientCredentials
     */
	public IdentityNowService() {
		this.creds = EnvironmentCredentialer.getEnvironmentCredentials();
		this.session = null;
	}
	
	/*
	 * Methods
	 */
	public SessionBase createSession() throws Exception {
		return this.session = SessionFactory.createSession(this.creds, SessionType.SESSION_TYPE_API_ONLY);
	}
  
	public <S> S getService ( Class<S> serviceClass ) throws Exception {		
		if ( session == null )
			createSession();		
		return ServiceFactory.getService( serviceClass, this.creds, this.session );
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
  
	public static <T> T execute ( Call<T> call ) throws Exception {	
		return call.execute().body();
	}
  
}