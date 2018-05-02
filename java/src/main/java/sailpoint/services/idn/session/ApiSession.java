package sailpoint.services.idn.session;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.interceptor.ApiCredentialsBasicAuthInterceptor;
import sailpoint.services.idn.sdk.object.ApiClientAuthorization;

/** 
 * A Session model for an API Gateway based IdentityNow session.
 * @author adam.hampton
 *
 */
public class ApiSession extends SessionBase {
	
	public final static Logger log = LogManager.getLogger(ApiSession.class);
	
	public static final String URL_SUFFIX_OAUTH_TOKEN  = "/oauth/token";   // Log-on
	public static final String URL_SUFFIX_OAUTH_REVOKE = "/oauth/revoke";  // Log-off
	
	protected ApiClientAuthorization apiClientAuth = null;
	
	public ApiSession (ClientCredentials clientCredentials) {
		
		super (clientCredentials);
		
		// Sanity check the arguments passed in.
		String clientId = clientCredentials.getClientId();
		if ((null == clientId) || (0 == clientId.length())) {
			throw new IllegalArgumentException("Client Credentials must contain a Client ID to construct an ApiSession");
		}
		String clientSecret = clientCredentials.getClientSecret(); 
		if ((null == clientSecret) || (0 == clientSecret.length())) {
			throw new IllegalArgumentException("Client Credentials must contain a Client Secret to construct an ApiSession");
		}
		
		this.setSessionType(SessionType.SESSION_TYPE_API_ONLY);
		
	}
	
	/**
	 * Authenticate via the API gateway to the IdentityNow organization.
	 * @throws IOException 
	 */
	@Override
	public SessionBase open() throws IOException {
		
		String oAuthUrl = creds.getGatewayUrl() + URL_SUFFIX_OAUTH_TOKEN;
		
		// TODO: Hook in here to honor proxy stuff; we're going to need a global clientBuilder.
		// Construct our OkHttpClient with pro-active Authorization header injection.
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.addInterceptor(new ApiCredentialsBasicAuthInterceptor(creds.getClientId(), creds.getClientSecret()));
		OkHttpClient client = clientBuilder.build();
		
		MediaType formMedia = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
		RequestBody requestBody = RequestBody.create(formMedia, "grant_type=client_credentials");
		
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(oAuthUrl);
		reqBuilder.addHeader("User-Agent", "Mozilla/5.0 (IdentityNow Services Chandlery SDK Client)");
		reqBuilder.addHeader("Accept", "*/*");
		reqBuilder.post(requestBody);
				
		Request request = reqBuilder.build();
		
		String responseJson = null;
		try (Response response = client.newCall(request).execute()) {
			responseJson = response.body().string();
			log.info("body: " + responseJson);
		} catch (IOException e) {
			log.error("Failure while calling " + oAuthUrl + " to login. ", e);
			throw e;
		}
		
		// Parse the response json that comes back.
		Gson gson = new Gson();
		ApiClientAuthorization aca = gson.fromJson(responseJson, ApiClientAuthorization.class);
		log.info("Successfully Authenticated to API for Org [" + aca.getOrg() + "], access token: " + aca.getAccessToken());
		
		// TODO: Why not only have the API Session reference the ApiClientAuthorization directly?
		this.apiClientAuth = aca;
		this.setAccessToken(aca.getAccessToken());
		this.setExpiresIn(aca.getExpiresIn());
		
		return this; 
		
	}
	
	@Override
	public void close() {
		log.warn("TODO: Implement close!");
	}
	
	public ApiClientAuthorization getApiClientAuthorization() {
		return apiClientAuth;
	}
	
	@Override
	public String getUniqueId() {
		if (null == apiClientAuth) return null;
		return apiClientAuth.getAccessToken();
	}
	

}
