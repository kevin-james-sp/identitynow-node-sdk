package sailpoint.services.idn.session;


import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.interceptor.BasicAuthInterceptor;
import sailpoint.services.idn.sdk.services.AuthorizationService;
import sailpoint.services.idn.util.ToJson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Factory for creating various types of IdentityNow sessions.
 * 
 * TODO: Think this out. 
 * There are multiple session types that can be used concurrently.
 *  - An API session can exist with no user session or user in context at all.
 *  - A simple auth UI session can exist with no API credentials in context.
 *  - A strong auth UI session can exist with no API credentials in context.
 *  - An oAuthToken CC API session must have an API key and users basic creds.
 *  This needs to be verified as of 7/30/2018
 * 
 * @author adam.hampton
 *
 */
public class SessionFactory {

	/**
	 * gson parser for parsing input objects to strings for sending http calls
	 */
	private static final Gson gson = new Gson();
	
	/**
	 * Assemble an IdentityNow API session from configured environment.
	 * @return
	 */
	public static SessionBase createApiSession () {
		return createSession(SessionType.SESSION_TYPE_API_ONLY); 
	}
	
	/**
	 * Assemble an IdentitNow UI Session from configured environment.
	 */
	public static SessionBase createUiSession () {
		return createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
	}
	
	/**
	 * Attempt to assemble an IdentityNow session from configured environment. 
	 * @return
	 */
	public static SessionBase createSession (SessionType sessionType) {
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
		return createSession (envCreds, sessionType);
	}
	
	/**
	 * Explicitly construct a session type with the given credentials set.
	 * @param sessionType
	 * @return
	 */
	public static SessionBase createSession (ClientCredentials clientCreds, SessionType sessionType) {
		switch (sessionType) {
//		case SESSION_TYPE_ADMIN_API_STRONG_AUTHN:
//			break;
		case SESSION_TYPE_UI_USER_BASIC:
			return new UserInterfaceSession(clientCreds);
		default:
		case SESSION_TYPE_API_ONLY:
			return new ApiSession(clientCreds);
		}		
	}
	
	// TODO:
	// public static Session createSession (ClientCredentials clientCredentials, SessionType)

//	public static Session createSession ( ClientCredentials clientCredentials) throws Exception {
//
//		if ( clientCredentials == null ) {
//			throw new Exception( "Invalid tenant configuration.  Expected non-null tenant configuration." );
//			return null;
//		}
//
//		return createSession( 
//				tenant.getUrl(), 
//				tenant.getUsername(), 
//				tenant.getPassword(), 
//				tenant.getApiUser(), 
//				tenant.getApiKey() );
//	}

	public static SessionBase createSession ( String url, String user, String hashedPassword, String apiUser, String apiKey ) throws Exception {

		SessionBase session = null;

		try {

			Response<SessionBase> response = SessionFactory
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
				.addConverterFactory(new Converter.Factory() {
					@Override
					public Converter<?, String> stringConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
						if (!hasToJson(annotations))
							return super.stringConverter(type, annotations, retrofit);
						return value -> gson.toJson(value, type);
					}

					private boolean hasToJson(final Annotation[] annotations){
						for (final Annotation annotation : annotations){
							if(annotation instanceof ToJson)
								return true;
						}
					return false;

					}
				})
				.addConverterFactory(GsonConverterFactory.create())
				.client( client )
				.build();

		return retrofit.create( serviceClass );
	}
}
