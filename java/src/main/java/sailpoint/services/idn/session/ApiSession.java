package sailpoint.services.idn.session;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.interceptor.ApiCredentialsBasicAuthInterceptor;
import sailpoint.services.idn.sdk.object.ApiClientAuthorization;
import sailpoint.services.idn.sdk.object.OAuthJwtResponse;

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
	
	private OkHttpClient apiGatewayClient = null;
	
	public ApiSession (ClientCredentials clientCredentials) {
		
		super (clientCredentials);
		
		// If we have a personal access token then use it for all of our API calls.
		String pat = clientCredentials.getPersAccTkn();
		if ((null !=  pat && 0 < pat.length())) {
			this.setSessionType(SessionType.SESSION_TYPE_PERSONAL_ACCESS_TOKEN);
			return;
		}
		
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
		
		// If we have a personal access token then handle this differently.
		// See: https://community.sailpoint.com/t5/IdentityNow-Wiki/Best-Practice-Using-Personal-Access-Tokens-in-IdentityNow/ta-p/150471#
		// We do an Oauth2.0 request with the personal access token to get a JWT.
		if (	
				(this.sessionType == SessionType.SESSION_TYPE_PERSONAL_ACCESS_TOKEN) &&
				(creds.hasPersonalAccessToken())
		) {
			log.debug("Using Personal Access Token for OAuth 2.0 Token Request.");
			this.setAccessToken(creds.getPersAccTkn());
			
			// POST https://{tenant}.api.identitynow.com/oauth/token?grant_type=client_credentials&client_id={client_id}&client_secret={client_secret}
			HttpUrl url = HttpUrl.parse(oAuthUrl).newBuilder()
					.addQueryParameter("grant_type",   "client_credentials")
					.addQueryParameter("client_id",     creds.getPersAccTkn())
					.addQueryParameter("client_secret", creds.getPersAccScr())
					.build();
			
			OkHttpClient client = new OkHttpClient.Builder().build();
			
			RequestBody emptyPostBody = RequestBody.create(null, new byte[]{});
			
			Request.Builder reqBuilder = new Request.Builder();
			reqBuilder.url(url);
			reqBuilder.addHeader("User-Agent", OkHttpUtils.getUserAgent());
			reqBuilder.addHeader("Accept", "*/*");
			reqBuilder.addHeader("cache-control", "no-cache");
			reqBuilder.post(emptyPostBody);
			
			Request request = reqBuilder.build();
			
			String responseJson = null;
			
			try (Response response = OkHttpUtils.callWithRetires(client, request)) {
				log.debug(String.format(
						"Sending body-free OAuth 2.0 token POST to %s with PAT id %s:%s",
						url,
						creds.getPersAccTkn(),
						creds.getPersAccScr()
				));
				responseJson = response.body().string();
				log.info("responseJson: " + responseJson);
				
				OAuthJwtResponse oajr = new Gson().fromJson(responseJson, OAuthJwtResponse.class);
				this.setOAuthBearerJwt(oajr);
				this.setAccessToken(oajr.getAccessToken());
				this.setExpiresIn(oajr.getExpiresIn());
				creds.setJWTToken(oajr.getAccessToken());
				
				log.debug("OAuth Access Token: " + oajr.getAccessToken());
				
			} catch (IOException e) {
				log.error("Failure while calling " + oAuthUrl + " to login. ", e);
				throw e;
			}
			
			return this;
		}
		
		// TODO: Hook in here to honor proxy stuff; we're going to need a global clientBuilder.
		// Construct our OkHttpClient with pro-active Authorization header injection.
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.addInterceptor(new ApiCredentialsBasicAuthInterceptor(creds.getClientId(), creds.getClientSecret()));
		OkHttpClient client = clientBuilder.build();
		
		MediaType formMedia = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
		RequestBody requestBody = RequestBody.create(formMedia, "grant_type=client_credentials");
		
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(oAuthUrl);
		reqBuilder.addHeader("User-Agent", OkHttpUtils.getUserAgent());
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
		this.isAthenticated = true;
		
		// TODO: Why not only have the API Session reference the ApiClientAuthorization directly?
		
		this.apiClientAuth = aca;
		this.setAccessToken(aca.getAccessToken());
		this.setExpiresIn(aca.getExpiresIn());
		creds.setJWTToken(aca.accessToken);
		
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
		if (creds.hasPersonalAccessToken()) return creds.getPersAccTkn();
		if (null == apiClientAuth) return null;
		return apiClientAuth.getAccessToken();
	}
	
	@Override
	public OkHttpClient getClient() {
		
		if (null != apiGatewayClient) return apiGatewayClient;
		
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		OkHttpUtils.applyTimeoutSettings(clientBuilder);
		OkHttpUtils.applyLoggingInterceptors(clientBuilder);
		OkHttpUtils.applyProxySettings(clientBuilder);
		clientBuilder.cookieJar(new JavaNetCookieJar(new CookieManager()));
		
		// Experiment with re-using a single connection for up to 10 seconds.
		ConnectionPool apiGwCxnPool = new ConnectionPool(1, 10, TimeUnit.SECONDS);
		clientBuilder.connectionPool(apiGwCxnPool);

		apiGatewayClient = clientBuilder.build();
		
		return apiGatewayClient;
	}
	
	public String doApiGet (String apiUrlSuffix) {
		
		String apiUrl = getApiGatewayUrl() + apiUrlSuffix;
		
		HashMap<String,String> apiHeadersMap = new HashMap<String,String>();
		apiHeadersMap.put("Authorization", "Bearer " + this.accessToken);
		// apiHeadersMap.put("User-Agent", OkHttpUtils.getUserAgent());
		
		try (Response response = doGet(apiUrl, getClient(), apiHeadersMap, null)) {
			return extractResponseString(response);
		} catch (IOException e) {
			log.error("Failure while calling " + apiUrl, e);
			return null;
		}
		
	}

	public String doApiPost (String apiUrlSuffix, Map<String,String> form) {
		String apiUrl = getApiGatewayUrl() + apiUrlSuffix;

		HashMap<String,String> apiHeadersMap = new HashMap<String,String>();
		apiHeadersMap.put("Authorization", "Bearer " + this.accessToken);

		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		for (String key : form.keySet()) {
			formBodyBuilder.add(key, form.get(key));
		}

		try(Response response = doPost(apiUrl, formBodyBuilder.build(), getClient(), apiHeadersMap, null)) {
			return extractResponseString(response);
		} catch (IOException e) {
			log.error("Failure while calling " + apiUrl, e);
			return null;
		}

	}

	private String extractResponseString(Response response) throws IOException {
		if (!response.isSuccessful()) {
			log.error(response.code() + " while calling " + response.request().url().toString());
		}
		String responseJson = response.body().string();
		response.body().close();
		// Spare the expensive string concat if we can:
		if (log.isDebugEnabled()) {
			log.debug(response.request().url().toString() + ": " + responseJson);
		}
		return responseJson;
	}
	
}
