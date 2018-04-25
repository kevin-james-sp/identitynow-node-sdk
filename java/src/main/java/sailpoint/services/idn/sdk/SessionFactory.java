package sailpoint.services.idn.sdk;


import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sailpoint.services.idn.sdk.interceptor.BasicAuthInterceptor;
import sailpoint.services.idn.sdk.object.Session;
import sailpoint.services.idn.sdk.object.Tenant;
import sailpoint.services.idn.sdk.services.AuthorizationService;

public class SessionFactory {

	public static Session createSession ( Tenant tenant ) throws Exception {

		if ( tenant == null )
			throw new Exception( "Invalid tenant configuration.  Expected non-null tenant configuration." );

		return createSession( 
				tenant.getUrl(), 
				tenant.getUsername(), 
				tenant.getPassword(), 
				tenant.getApiUser(), 
				tenant.getApiKey() );
	}

	public static Session createSession ( String url, String user, String hashedPassword, String apiUser, String apiKey ) throws Exception {

		Session session = null;

		try {

			Response<Session> response = SessionFactory
					.createService( AuthorizationService.class, url, apiUser, apiKey )
					.getSession( "password", user, hashedPassword )
					.execute();
			
			if ( response.isSuccessful() )
				session = response.body();
			else
				throw new Exception ( "Error obtaining session! " + response.code() + " " + response.message() );

		} catch ( IOException e ) {
			e.printStackTrace();
		}

		return session;
	}
	
	public static <S> S createService( Class<S> serviceClass, String url, String username, String password ) {

		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor( new BasicAuthInterceptor( username, password ) )
			.build();

		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl( url )
			.addConverterFactory( GsonConverterFactory.create() )
			.client( client )
			.build();

		return retrofit.create( serviceClass );
	}
}
