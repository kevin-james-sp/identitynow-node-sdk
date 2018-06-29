package sailpoint.services.idn.session;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.interceptor.ApiCredentialsBasicAuthInterceptor;
import sailpoint.services.idn.sdk.interceptor.JwtBearerAuthInterceptor;
import sailpoint.services.idn.sdk.interceptor.LoggingInterceptor;
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
	public static final String URL_LOGIN_GET   = "login/get";
	public static final String URL_UI          = "ui";
	
	public String ssoUrl = null;
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
	 * This routine applies an SHA-256 has to the given string.
	 * SHA-256 hashing is applied in at least two places in the IdentityNow 
	 * user interface.  When a user initially logs in a sequence of hashing
	 * is applied and when a user Strong-Authenticates to the Administrative
	 * user interface their "KBA" (knowledge-based-authentication) answers
	 * are toLowerCase()-ed and hashed before sending up to the server.
	 * @param argString
	 * @return
	 */	
	public static String sha256Hash(String argString) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(argString.getBytes(StandardCharsets.UTF_8));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e1) {
			log.error("Failure SHA-256 hashing a string", e1);
		}
		return "";
	}
	
	/**
	 * IdentityNow uses a hash of the the user's name to salt the hash of a 
	 * user's password or strong-authentication-question result.  This function
	 * applies that salting algorithm to the value passed in. 
	 * @param user
	 * @param valueToHash
	 * @param doDebug
	 * @return
	 */	
	public static String applySaltedHash(String user, String valueToHash) {
		String preHashPayload = valueToHash + sha256Hash(user.toLowerCase());
		log.debug("preHashPayload: " + preHashPayload);
		String completeHashPayload = sha256Hash(preHashPayload);
		log.debug("completeHashPayload: " + completeHashPayload);
		return completeHashPayload;		
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
	/* (non-Javadoc)
	 * @see sailpoint.services.idn.session.SessionBase#open()
	 */
	@Override 
	public UserInterfaceSession open() throws IOException {
		
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
		
		// TODO: Use a common client builder that includes user-agents, etc.
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		// if (log.isDebugEnabled()) {
			clientBuilder.addInterceptor(new LoggingInterceptor());
		// }
		OkHttpClient client = clientBuilder.build();
		
		// OkHttpClient client = new OkHttpClient();
		
		// STEP 1: Call /login/login and extract the API Gateway URL for the org and other data.
		Builder reqBuilder = new Request.Builder();
		String uiUrl = getUserInterfaceUrl() + URL_LOGIN_LOGIN;
		log.debug("Attempting to login to: " + uiUrl);
		reqBuilder.url(uiUrl);
		
		Request request = reqBuilder.build();
		Gson gson = new Gson();
		UiSailpointGlobals apiSlptGlobals = null;

		Response response = client.newCall(request).execute();
		String respHtml = response.body().string();
		log.trace("respString: " + respHtml);
		
		// Handle various response / error conditions.
		switch (response.code()) {
		case 403:
			String errMsg = "403 while GET'ing " + uiUrl + " - Invalid client regional IP or VPN disconnected?"; 
			log.error(errMsg);
			throw new IOException(errMsg);
		}
		
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
		
		apiSlptGlobals = gson.fromJson(jsonBody, UiSailpointGlobals.class);
		this.setApiGatewayUrl(apiSlptGlobals.getApi().getBaseUrl());
		log.debug("API URL:" + this.getApiGatewayUrl());
			
	
		// STEP 2: Make a POST to /login/get to get the properties for the user.
		String jsonContent = "{username=" + getCredentials().getOrgUser() + "}";

		uiUrl = getUserInterfaceUrl() + URL_LOGIN_GET;
		response = doPost(uiUrl, jsonContent, client);
		String responseBody = response.body().string();
		log.debug(responseBody);
		UiLoginGetResponse apiLoginGetResponse = gson.fromJson(responseBody, UiLoginGetResponse.class);
		log.debug("encryption type = " + apiLoginGetResponse.getApiAuth().getEncryptionType());
		
		this.ssoUrl = apiLoginGetResponse.getSsoServerUrl();
		log.debug("ssoUrl = " + ssoUrl);
		
		String onFailUrl = apiLoginGetResponse.getGoToOnFail();
		if (null == onFailUrl) {
			onFailUrl = apiSlptGlobals.getGotoOnFail();
		}
		
		// STEP 3: Make a POST to the SSO path for the org.
		// This makes a POST to the SSO login service which at one point was implemented on OpenAM.  
		// This requires Form formatted inputs and some obtuse-ly named fields like "IDToken2".
		RequestBody formBody = new FormBody.Builder()
				.add("encryption", apiLoginGetResponse.getApiAuth().getEncryptionType())
				.add("service",    apiLoginGetResponse.getApiAuth().getService())
				.add("IDToken1",   creds.getOrgUser())
				.add("IDToken2",   applySaltedHash(creds.getOrgUser(), creds.getOrgPass()))
				.add("realm",      apiSlptGlobals.getOrgScriptName())
				.add("goto",       creds.getUserIntUrl() + URL_UI)
				.add("gotoOnFail", onFailUrl) 
				.add("openam.session.persist_am_cookie", "true")
				.build();
		
		RequestBody test = new FormBody.Builder(Charset.forName("UTF-8"))
				.add("foo", "bar")
				.add("baz", "bat")
				.build();
		
		FormBody.Builder formBuilder = new FormBody.Builder()
		        .add("key", "123");
		formBuilder.addEncoded("baz", "bat");
		
	      
		
		log.debug("formBody: " + formBody.contentLength() + " " + formBody.contentType());
		log.debug("formBody: " + test.contentLength() + " " + test.contentType());
		
		// response = doPost(ssoUrl, formBody, client);
		response = doPost(ssoUrl, formBuilder.build(), client);
		String ssoResponse = response.body().string();
		
		log.debug("response code: " + response.code() + " .isRedirect():" + response.isRedirect());
		
		log.debug("SSO Response Body:" + ssoResponse);
		
		return null;
	}
	
	@Override
	public void close() {
		log.warn("TODO: Implement close() for user interface type sessions!");
	}

}
