package sailpoint.services.idn.session;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import sailpoint.services.idn.sdk.ClientCredentials;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.Request;

/**
 * The top level class from which all other Session types derive. 
 * 
 * @author adam.hampton
 *
 */
public class SessionBase implements java.lang.AutoCloseable {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	protected ClientCredentials creds = null;
	
	// Default to API Only session types for SDK use.
	protected SessionType sessionType = SessionType.SESSION_TYPE_API_ONLY;
	
	protected String accessToken = null;
	protected int expiresIn = -1;
	protected boolean isAthenticated = false;
	
	public SessionBase (ClientCredentials clientCredentials) {
		if (null == clientCredentials.getOrgName()) {
			throw new IllegalArgumentException("ClientCredentials must contain an Organization Name to construct a Session.");
		}
		this.creds = clientCredentials;
	}
	
	/**
	 * Connect to the IdentityNow service and establish the session.  This "logs in"
	 * using whatever means the session has at its disposal to connect to the service.
	 * 
	 * Returns a self-reference for chain-able operations.
	 */
	public SessionBase open() throws IOException {
		throw new IllegalArgumentException("Session sub-classes must implement their own open() methods.");
	}
	
	/**
	 * Return a new unique ID for the connected session; depends on session type being established. 
	 * @return
	 */
	public String getUniqueId() {
		throw new IllegalArgumentException("Session sub-classes must implement their own getUniqueId() methods.");
	}

	public SessionType getSessionType() {
		return sessionType;
	}

	public void setSessionType(SessionType sessionType) {
		this.sessionType = sessionType;
	}

//	public ClientCredentials getCreds() {
//		return creds;
//	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	@Override
	public void close() throws Exception {
		throw new IllegalArgumentException("Session sub-classes must implement their own close() methods.");
	}
	
	public boolean isAuthenticated() {
		return isAthenticated;
	}
	
	protected ClientCredentials getCredentials() {
		return creds;
	}
	
	public String getApiGatewayUrl() {
		return creds.getGatewayUrl();
	}
	
	/**
	 * Returns the user interface URL for the org in question.  This may look like:
	 *  - https://dev02-useast1.cloud.sailpoint.com/perflab-05121107
	 *  - TODO: Add staging url example:
	 *  - https://neil-test.identitynow.com/
	 *  - https://sailpoint.identitynow.com/
	 * @return
	 */
	public String getUserInterfaceUrl() {
		if (!creds.getUserIntUrl().endsWith("/")) {
			return creds.getUserIntUrl() + "/";
		}
		return creds.getUserIntUrl();
	}
	
	public OkHttpClient getClient() {
		throw new IllegalArgumentException("Session sub-classes must implement their own getClient() methods.");
	}
	
	protected Response doGet (String url, OkHttpClient client) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		return client.newCall(request).execute();
	}

	/**
	 * Make a JSON based POST to the given URL.
	 * 
	 * @param url
	 * @param json
	 * @param client
	 * @return
	 * @throws IOException
	 */
	protected Response doPost(String url, String json, OkHttpClient client) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		return client.newCall(request).execute();
	}
	
	/**
	 * Make an FormBody type POST to the given URL.
	 * @param url
	 * @param formBody - the payload and it's content type to deliver to the server 
	 * @param client
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	protected Response doPost(String url, RequestBody formBody, OkHttpClient client, Map<String,String> headers) throws IOException {
		Request.Builder builder = new Request.Builder();
		OkHttpUtils.appendHeaders(builder, OkHttpUtils.getDefaultHeaders());
		if ((null != headers) && (!headers.isEmpty())) {
			OkHttpUtils.appendHeaders(builder, headers);
		}
		builder.url(url);
		builder.post(formBody);
		Request request = builder.build();
		return client.newCall(request).execute();
	}
	
	// Whatever comes back from the UI can override the API Gateway URL setting.
	protected void setApiGatewayUrl(String newGwUrl) {
		creds.setGatewayUrl(newGwUrl);
	}

}
