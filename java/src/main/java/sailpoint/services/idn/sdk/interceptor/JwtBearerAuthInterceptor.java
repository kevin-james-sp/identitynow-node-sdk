package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Define an interceptor that injects Authorization Bearer JWT token headers.
 * @author adam.hampton
 *
 */
public class JwtBearerAuthInterceptor implements Interceptor {
	
	public final static Logger log = LogManager.getLogger(JwtBearerAuthInterceptor.class);

	private String jwtAccessToken;

	public JwtBearerAuthInterceptor(String jwtAccessToken) {
		if ((null == jwtAccessToken) || (0 == jwtAccessToken.length())) {
			throw new IllegalArgumentException("JWT Access token must be populated.");
		}
		this.jwtAccessToken = jwtAccessToken;
	}
	
	@Override
	public Response intercept(Chain chain) throws IOException {
		log.debug("Authorizing transaction with JWT: " + jwtAccessToken);
		Request request = chain.request();
		Request authenticatedRequest = request.newBuilder().header("Authorization", "Bearer " + jwtAccessToken).build();
		return chain.proceed(authenticatedRequest);
	}

}