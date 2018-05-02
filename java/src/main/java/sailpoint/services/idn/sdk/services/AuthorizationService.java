package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import sailpoint.services.idn.session.SessionBase;

public interface AuthorizationService {
	
	//{{url}}/api/oauth/token?grant_type=password&username=neil.mcglennon&password=14f...8d6  
	@GET( "/api/oauth/token" )
	Call<SessionBase> getSession( 
		@Query( "grant_type" ) String grantType,
		@Query( "username" ) String username,
		@Query( "password" ) String password );
}
