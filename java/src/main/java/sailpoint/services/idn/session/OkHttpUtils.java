package sailpoint.services.idn.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.interceptor.ApiCredentialsBasicAuthInterceptor;
import sailpoint.services.idn.sdk.interceptor.JwtBearerAuthInterceptor;
import sailpoint.services.idn.sdk.interceptor.LoggingInterceptor;

/**
 * Convenience methods for repeatedly constructing OkHttp infrastructure to call
 * into an IdentityNow organization.
 * 
 * Responsible for configuring
 * 
 * - Default Client client types. - Default authentication interceptors. -
 * Default JWT token injection for the session.
 * 
 * TODO: Add Default proxy configuration to this client as well.
 * 
 * @author adam.hampton
 *
 */
public class OkHttpUtils {

	public final static Logger log = LogManager.getLogger(OkHttpUtils.class);

	// Build out the user agent string once per JVM session.
	public final static String USER_AGENT = 
			"Mozilla/5.0 (IdentityNow Services Chandlery SDK Client on "
			+ System.getProperty("os.name") + " " + System.getProperty("os.version") 
			+ " java:" + System.getProperty("java.version")
			// + " host:" + InetAddress.getLocalHost().getHostName() // <-- Debate-able
			+ ")";

	// IdentityNow's API gateway can send 429s with a recommendation for how
	// long to delay the retry in the 'Retry-After' header. The value is
	// specified in seconds to delay.
	public static final String HEADER_RETRY_AFTER = "Retry-After";

	// The maximum number of times the API client will retry a request when a 429
	// result us returned by the IdentityNow API server.
	public static final int MAX_429_RETRIES = 3;

	// How many milliseconds will we wait before re-submitting our request to API.
	public static final int RETRY_429_DELAY_MS_DEFAULT = 3000;
	
	SessionBase session;
	
	// Return the Chandlery user agent for other HTTP clients to use.
	public static String getUserAgent() {
		return USER_AGENT;
	}

	public OkHttpUtils(SessionBase session) {
		this.session = session;
	}

	public OkHttpClient.Builder getClientBuilder() {

		ClientCredentials creds = session.getCredentials();

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		
		log.debug("Adding Authorization interceptor for Session type " + session.getSessionType().toString());
		switch (session.getSessionType()) {
		default:
		case SESSION_TYPE_API_ONLY:
			clientBuilder.addInterceptor(new JwtBearerAuthInterceptor(session.getAccessToken()));
			break;
		case SESSION_TYPE_API_WITH_USER:
			clientBuilder.addInterceptor(new JwtBearerAuthInterceptor(session.getAccessToken()));
			break;
		case SESSION_TYPE_UI_USER_BASIC:
			log.warn("TODO: test this case.");
			clientBuilder.addInterceptor(
					new ApiCredentialsBasicAuthInterceptor(creds.getOrgUser(), creds.getOrgPass())
			);
			break;
		case SESSION_TYPE_UI_USER_STRONG_AUTHN:
			log.warn("TODO: test this case.");
			clientBuilder.addInterceptor(
					new ApiCredentialsBasicAuthInterceptor(creds.getOrgUser(), creds.getOrgPass())
			);
			break;
		}
		
		clientBuilder.addInterceptor(new LoggingInterceptor());
		
		return clientBuilder;

	}

	public Request.Builder getRequestBuilder(String url) {
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(url);
		reqBuilder.addHeader("User-Agent", USER_AGENT);
		return reqBuilder;
	}
	
	/**
	 * Returns the default map of HTTP headers used by OkHttp clients.
	 * @return
	 */
	public static HashMap<String, String> getDefaultHeaders() {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("User-Agent", USER_AGENT);
		// Required so we don't blow up older Pendo integrations.
		map.put("Accept-Language",  "en-US,en;q=0.5");
		return map;
	}
	
	/**
	 * Append header information from the provided map to the given request builder.
	 * @param builder
	 * @param headers
	 */
	public static void appendHeaders (Request.Builder builder, Map<String, String> headers) {
		Objects.requireNonNull(builder);
		Headers.Builder headerBuilder = new Headers.Builder();
		if (headers == null || headers.isEmpty())
			return;
		for (String key : headers.keySet()) {
			headerBuilder.add(key, headers.get(key));
		}
		builder.headers(headerBuilder.build());
	}
	
	/**
	 * Append a single header to a request builder. 
	 * WARNING: this is relatively inefficient; call the Map<> based version instead.
	 * @param builder
	 * @param headerName
	 * @param headerValue
	 */
	public static void appendHeader (Request.Builder builder, String headerName, String headerValue) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(headerName, headerValue);
		appendHeaders(builder, map);
	}

	/**
	 * Lower level call to OkHTTP to transact an HTTP transaction. 
	 * Automatically handles 429 retry requests from the API gateway.
	 * Handles all transaction types, GET/POST/PUT/etc.
	 * 
	 * TODO: Decide how to handle and various responses. - In the event of 429
	 * exhaustion should this except out? - In the event of a 500 error should we
	 * except out?
	 * 
	 * @param getRequest
	 * @return
	 */
	public Response callWithRetires(OkHttpClient okClient, Request getRequest) {

		Response response = null;
		int attemptCount = 0;
		int responseCode = 500;
		do {
			try {
				response = okClient.newCall(getRequest).execute();
				responseCode = response.code();
				if (200 == responseCode) {
					break; // out of do-while() loop.
				}
			} catch (IOException e) {
				log.error("Failure while calling " + getRequest.url().toString(), e);
				return null;
			}

			if (429 == responseCode) {
				// Look for any header returned like: Retry-After = 1 where the server
				// advises us as a client for how long to wait to back-off requests.
				int retryDelay = RETRY_429_DELAY_MS_DEFAULT;
				String retryAfter = response.header(HEADER_RETRY_AFTER);
				if ((null != retryAfter) && (0 != retryAfter.length())) {
					retryDelay = 1000 * (Integer.parseInt(retryAfter));
				}

				String reponseBody = response.body().toString();

				if (attemptCount < MAX_429_RETRIES) {
					log.warn("429 - Rate Limit Exceeded: " + reponseBody + " retrying in " + retryDelay
							+ " msecs, attemptCount:" + attemptCount);
					try {
						Thread.sleep(retryDelay);
					} catch (InterruptedException e) {
						log.error("Failure while waiting for an HTTP 429 retry", e);
					}
				} else {
					log.error("429 - Rate Limit Exceeded: " + reponseBody + " attemptCount:" + attemptCount
							+ " retries exhausted.");
					break; // out of do-while() loop.
				}

			} else {
				log.error("Unexpected HTTP Resposne: " + responseCode + " for " + getRequest.url());
				break; // out of do-while() loop.
			}

		} while (429 != responseCode);

		return response;

	}

}
