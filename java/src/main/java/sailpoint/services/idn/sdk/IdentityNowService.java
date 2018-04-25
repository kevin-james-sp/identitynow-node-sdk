package sailpoint.services.idn.sdk;

import retrofit2.Call;
import sailpoint.services.idn.sdk.object.Session;
import sailpoint.services.idn.sdk.object.Tenant;
import sailpoint.services.idn.sdk.services.ConnectorService;
import sailpoint.services.idn.sdk.services.IdentityService;
import sailpoint.services.idn.sdk.services.ReportService;
import sailpoint.services.idn.sdk.services.TransformService;

public final class IdentityNowService {
	
	/*
	 * Instance variables
	 */
	
    private Tenant tenant;
    
    private Session session;
    
    /*
     * Constructors
     */
    
    public IdentityNowService( String url, String username, String password, String apiUser, String apiKey ) throws Exception {
		this.tenant = new Tenant( url, username, password, apiUser, apiKey );
		this.session = null;
	}
    
	public IdentityNowService( Tenant tenant ) {
		this.tenant = tenant;
		this.session = null;
	}
	
	/*
	 * Methods
	 */
	
	public Session createSession() throws Exception {
		return this.session = SessionFactory.createSession( this.tenant );
	}
  
	public <S> S getService ( Class<S> serviceClass ) throws Exception {
		
		if ( session == null )
			createSession();
		
		return ServiceFactory.getService( serviceClass, this.tenant, this.session );
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