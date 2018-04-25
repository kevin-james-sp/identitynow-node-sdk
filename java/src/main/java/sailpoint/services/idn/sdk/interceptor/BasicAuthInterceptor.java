package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import sailpoint.services.idn.sdk.Constants;

public class BasicAuthInterceptor implements Interceptor {

	private String username;
	private String password;
	
	public Response intercept( Chain chain ) throws IOException {
	
		final String credentials = username + ":" + password;
		final String authorization = "Basic " + Base64.getEncoder().encodeToString( credentials.getBytes( StandardCharsets.UTF_8 ) );
		
		Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
        	.addHeader( Constants.HTTP_HEADER_AUTHORIZATION_KEY, authorization )
        	.addHeader( Constants.HTTP_HEADER_CACHE_CONTROL_KEY, Constants.HTTP_HEADER_CACHE_CONTROL_VALUE )
        	.addHeader( Constants.HTTP_HEADER_CSRF_TOKEN_KEY, Constants.HTTP_HEADER_CSRF_TOKEN_VALUE )
            .method( original.method(), original.body() );

        Request request = requestBuilder.build();
        return chain.proceed(request);
	}

	public BasicAuthInterceptor( String username, String password ) {
		super();
		this.username = username;
		this.password = password;
	}
	
}
