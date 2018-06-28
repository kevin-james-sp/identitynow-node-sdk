package sailpoint.services.idn.session;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.object.UiSailpointGlobals;
import sailpoint.services.idn.sdk.object.UiAuthData;
import sailpoint.services.idn.sdk.object.UiLoginGetResponse;
import sailpoint.services.idn.sdk.object.UiOrgData;

/**
 * A model of a Session based on a user interface session for a specific user.
 * 
 * @author adam.hampton
 *
 */
public class UserInterfaceSession extends SessionBase {
	
	public final static Logger log = LogManager.getLogger(UserInterfaceSession.class);
	
	public static final String URL_LOGIN_LOGIN = "login/login";
	public static final String URL_LOGIN_GET = "login/get";
	
	public String ccSessionId = null;
	public String csrfToken = null;
	public String oauthToken = null;
	
	public UserInterfaceSession (ClientCredentials clientCredentials) {
		
		super (clientCredentials);
		
		// Sanity check the arguments passed in.
		String orgUser = clientCredentials.getOrgUser(); 
		if ((null == orgUser) || (0 == orgUser.length())) {
			throw new IllegalArgumentException("Client Credentials must contain a User to construct a UserInterfaceSession");
		}
		String orgPass = clientCredentials.getOrgPass();
		if ((null == orgPass) || (0 == orgPass.length())) {
			throw new IllegalArgumentException("Client Credentials must contain a User password to construct a UserInterfaceSession");
		}
		
		this.setSessionType(SessionType.SESSION_TYPE_UI_USER_BASIC);
	}
	
	@Override
	public String getUniqueId() {
		return ccSessionId;
	}
	
	public OkHttpClient getClient() {
		throw new IllegalArgumentException("TODO: Stub this out for UserInterfaceSession");
	}
	
	/**
	 * Connect to the IdentityNow service and establish the session.  This "logs in"
	 * using whatever means the session has at its disposal to connect to the service.
	 * 
	 * If the user is redirected to a registration page then currently nothing is done.
	 * 
	 * If there is a KBA map in context in the user's credentials then the user is
	 * strongly authenticated to their UI session.
	 * 
	 * If there is a Client ID and Client Secret (API User + API Key) in context then
	 * an OAuth token is generated for the user's session.
	 * 
	 * Returns a self-reference for chain-able operations.
	 */
	@Override 
	public UserInterfaceSession open() throws IOException{
		
		// The old (pre-oauth) sequence looks like the following:
		// 1. GET  /login/login -- pulls back the org login parameters.
		// 2. POST /login/get   -- pulls back specific properties for the user logging in (hash vs. key password, etc).
		// 3. POST ${SSO Path}  -- makes a POST to the SSO (OpenAM/etc) login service with credentials.
		// 4. Follow the redirects from the SSO login.  This usually takes the browser through /ui/ and to /ui/main/.
		// 5. Check for KBA map and if present do strong Auth-N.
		// 6. Check for API credentials and if present then do OAuth token for session.
		
		// The new (with oauth) sequence looks like:
		// 1. GET  /login/login -- pulls back the org login parameters and API Gateway URL.
		// 2. POST /login/get   -- pulls back specific properties for the user logging in (hash vs. key password, etc).
		// 3. POST ${SSO Path}  -- makes a POST to the SSO (OpenAM/etc) login service with credentials.
		// 4. Follow the redirects from the SSO login.  
		//    This usually takes the browser through /ui/ then to /oauth/authorize? and /oauth/callback?
		//    Then finally to /ui and then /main
		// 5. Check for KBA map and if present do strong Auth-N.
		// 6. Check for API credentials and if present then do OAuth token for session.
		
		OkHttpClient client = new OkHttpClient();
		
		// STEP 1: Call /login/login and extract the API Gateway URL for the org and other data.
		Builder reqBuilder = new Request.Builder();
		String uiUrl = getUserInterfaceUrl() + URL_LOGIN_LOGIN;
		log.debug("Attempting to login to: " + uiUrl);
		reqBuilder.url(uiUrl);
		
		Request request = reqBuilder.build();
		Response response;
		Gson gson = new Gson();

		try {
			response = client.newCall(request).execute();
			String respHtml = response.body().string();
			log.trace("respString: " + respHtml);
			
			// Note: we parse out the several fields from a JSON field that comes back in HTTP.
			Document doc = Jsoup.parse(respHtml);
			
			String selectorString = "script[id='slpt-globals-json']";
			Elements slptScript = doc.select(selectorString);
			if (null == slptScript) {
				log.error("Failure extracting slpt-globals-json with selector: " + selectorString);
				return null;
			}
			
			String jsonBody = slptScript.html();
			log.debug("slptScript:" + jsonBody);
			
			UiSailpointGlobals apiSlptGlobals = gson.fromJson(jsonBody, UiSailpointGlobals.class);
			this.setApiGatewayUrl(apiSlptGlobals.getApi().getBaseUrl());
			log.debug("API URL:" + this.getApiGatewayUrl());
			
			
		} catch (IOException e) {
			log.error("Failure GET'ing login/login page", e);
		}
	
		// STEP 2: Make a POST to /login/get to get the properties for the user.
		String jsonContent = "{username=" + getCredentials().getOrgUser() + "}";

		uiUrl = getUserInterfaceUrl() + URL_LOGIN_GET;
		response = doPost(uiUrl, jsonContent, client);
		String responseBody = response.body().string();
		log.debug(responseBody);
		UiLoginGetResponse apiLoginGetResponse = gson.fromJson(responseBody, UiLoginGetResponse.class);
		log.debug("encryption type = " + apiLoginGetResponse.getApiAuth().getEncryptionType());
		log.debug("ssoUrl = " + apiLoginGetResponse.getSsoServerUrl());

		return null;
	}
	
	@Override
	public void close() {
		log.warn("TODO: Implement close() for user interface type sessions!");
	}

}
