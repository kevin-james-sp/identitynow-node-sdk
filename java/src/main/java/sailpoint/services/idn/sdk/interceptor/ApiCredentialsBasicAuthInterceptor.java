package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Define an interceptor that injects Basic Authentication headers 
 * @author adam.hampton
 *
 */
public class ApiCredentialsBasicAuthInterceptor implements Interceptor {

	private String credentials;

	public ApiCredentialsBasicAuthInterceptor(String user, String password) {
		this.credentials = Credentials.basic(user, password);
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Request authenticatedRequest = request.newBuilder().header("Authorization", credentials).build();
		return chain.proceed(authenticatedRequest);
	}

}