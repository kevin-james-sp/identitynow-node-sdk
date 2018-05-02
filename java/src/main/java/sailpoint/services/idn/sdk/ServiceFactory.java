package sailpoint.services.idn.sdk;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sailpoint.services.idn.sdk.interceptor.BearerAuthInterceptor;
import sailpoint.services.idn.session.SessionBase;

public class ServiceFactory {

	public static <S> S getService ( Class<S> serviceClass, ClientCredentials creds, SessionBase session ) {
		return getService( 
				serviceClass, 
				creds.getUserIntUrl(),
				session.getAccessToken() );	
	}
	
	public static <S> S getService ( Class<S> serviceClass, String url, String token ) {

		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor( new BearerAuthInterceptor( token ) )
			.build();

		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl( url )
			.addConverterFactory( GsonConverterFactory.create() )
			.client( client )
			.build();

		return retrofit.create( serviceClass );
	}
}
