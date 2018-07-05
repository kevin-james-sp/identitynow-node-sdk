package sailpoint.services.idn.session;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.object.UiSailpointGlobals;
import sailpoint.services.idn.sdk.object.UiLoginGetResponse;

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
	public static final String URL_LOGOUT      = "logout";
	
	public String ssoUrl = null;
	public String ccSessionId = null;
	public String csrfToken = null;
	public String oauthToken = null;
	
	// Durations for performance analysis.
	public long loginSequenceDuration = 0;
	public long logoutSequenceDuration = 0; 
	
	CookieManager cookieManager = new CookieManager();
	
	public UserInterfaceSession (ClientCredentials clientCredentials) {
		
		super (clientCredentials);
		
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
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
		OkHttpUtils.applyTimeoutSettings(clientBuilder);
		OkHttpUtils.applyLoggingInterceptors(clientBuilder);
		clientBuilder.cookieJar(new JavaNetCookieJar(cookieManager));
			
		OkHttpClient client = clientBuilder.build();
		
		// STEP 1: Call /login/login and extract the API Gateway URL for the org and other data.
		
		String uiUrl = getUserInterfaceUrl() + URL_LOGIN_LOGIN;
		log.debug("Attempting to login to: " + uiUrl);
		
		long loginSequenceStartTime = System.currentTimeMillis();
		
		Response response = doGet(uiUrl, client, null, null);
		
		Gson gson = new Gson();
		UiSailpointGlobals apiSlptGlobals = null;
		
		String respHtml = response.body().string();
		log.trace("respString: " + respHtml);
		
		// Handle various response / error conditions.
		switch (response.code()) {
		default:
			String defMsg = response.code() + " while GET'ing " + uiUrl + " - HTTP communication error."; 
			log.error(defMsg);
			throw new IOException(defMsg);
		case 403:
			String errMsg = "403 while GET'ing " + uiUrl + " - Invalid client regional IP or VPN disconnected?"; 
			log.error(errMsg);
			throw new IOException(errMsg);
		case 200:
			// fall through to logic below.
			break;
		}
		
		// Pull out the CCSESSIONID cookie, we need to pass this to the SSO server.
		HttpCookie ccSessionCookie = null;
		for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
			if ( !"CCSESSIONID".equals(cookie.getName())) continue;
			ccSessionCookie = cookie;
		}
		ccSessionId = ccSessionCookie.getValue();
		log.debug("ccSessionId: " + ccSessionCookie.toString());
		
		// Parse out the several fields from a JSON field that comes back in HTTP.
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
		// The API Gateway is a _different_ URL that the browser uses a CORS 
		// request to access. In this code we will use a second, different,
		// client object.  Here's why:
		// When calling CC via the API gateway the gateway drops all cookies. 
		// So when CC gets the request it's like oh, cool I've never seen you 
		// before lets create a session together and it creates an new CCSESSIONID.		
		// You can safely ignore ccsessionid returned by all the API GW CC calls.
		// You can ignore any cookie from: 
		//     *.api.identitynow.com/cc/* 
		//  or *.api.identitynow.com/v2/*
		// So we build a new cookie manager here:
		OkHttpClient.Builder apiGwClientBuilder = new OkHttpClient.Builder();
		OkHttpUtils.applyTimeoutSettings(apiGwClientBuilder);
		OkHttpUtils.applyLoggingInterceptors(apiGwClientBuilder);
		apiGwClientBuilder.cookieJar(new JavaNetCookieJar(new CookieManager()));
		OkHttpClient apiGwClient = apiGwClientBuilder.build(); 
		
		String jsonContent = "{username=" + getCredentials().getOrgUser() + "}";

		uiUrl = getApiGatewayUrl() + "/cc/" + URL_LOGIN_GET;
		
		response = doPost(uiUrl, jsonContent, apiGwClient, null, null);
		String responseBody = response.body().string();
		log.debug(responseBody);
		
		UiLoginGetResponse apiLoginGetResponse = gson.fromJson(responseBody, UiLoginGetResponse.class);
		log.debug("encryption type = " + apiLoginGetResponse.getApiAuth().getEncryptionType());
		
		this.ssoUrl = apiLoginGetResponse.getSsoServerUrl() + "/login";
		log.debug("ssoUrl = " + ssoUrl );
		
		String onFailUrl = apiLoginGetResponse.getGoToOnFail();
		if (null == onFailUrl) {
			onFailUrl = apiSlptGlobals.getGotoOnFail();
		}
		
		// Host header should be the SSO server's host name to make CloudFront happy.
		String hostHeader = apiLoginGetResponse.getSsoServerUrl();
		if (hostHeader.contains("//")) {
			hostHeader = hostHeader.split("\\/\\/")[1];
			hostHeader = hostHeader.split("\\/")[0];
		}
		
		String originHeader = creds.getUserIntUrl();
		String originSuffix = "/" + apiSlptGlobals.getOrgScriptName() + "/";
		if (originHeader.endsWith(originSuffix)) {
			originHeader = originHeader.replace(originSuffix, "");
		}
		
		// STEP 3: Make a POST to the SSO path for the org.
		// This makes a POST to the SSO login service which at one point was implemented on OpenAM.  
		// This requires Form formatted inputs and some obtuse-ly named fields like "IDToken2".

		// Emulate the request properties that Firefox or Chrome post the the server.
		// OkHttpUtils okUtils = new OkHttpUtils(this);
		
		HashMap<String, String> headers = OkHttpUtils.getDefaultHeaders();		
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Encoding", "identity");
		headers.put("Cache-Control", "no-cache");
		headers.put("Host", hostHeader);
		headers.put("Origin", originHeader);
		headers.put("Pragma", "no-cache");
		headers.put("Referer", creds.getUserIntUrl() + "login/login?prompt=true");
		headers.put("DNT", "1");
		headers.put("Upgrade-Insecure-Requests", "1");
		
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
		
		log.debug("formBody: " + formBody.contentLength() + " " + formBody.contentType());
		
		// Add the ccSessionId cookie to the POST request to the SSO server.
		// This is "interesting" in OkHttp3 due to the 302s that follow.
		// For more info: https://github.com/request/request/issues/1502
		// Instead we roll our own redirect handler here.
		OkHttpClient manual302Client = clientBuilder.followRedirects(false).build();
		response = doPost(ssoUrl, formBody, manual302Client, headers, cookieManager.getCookieStore().getCookies());
		
		// TODO: Support encryption types other than "hash".
		// TODO: Check for basic success/failure of the authentication.
		
		// Extract the cookies from the SSO call.
		Map<String, List<String>> cookieHdrs = new HashMap<String, List<String>>();
		cookieHdrs.put("Cookie", new ArrayList<String>(response.headers("Set-Cookie")));
		try {
			cookieManager.put(new URI(ssoUrl), cookieHdrs);
		} catch (URISyntaxException e) {
			log.error("cookie error", e);
		}
		
		// Follow 302s from the user interface to the Launchpad / Dashboard screen.
		// Parse out various useful bits of OAuth information along the way.
		boolean redirectsDone = false;
		String nextUrl = response.header("Location");
		do {
			response = doGet(nextUrl, manual302Client, null, cookieManager.getCookieStore().getCookies());
			switch (response.code()) {
			case 302:
				nextUrl = response.header("Location");
				break;
			case 403:
				String errMsg = "Failure while following redirects from SSO; unable to login";
				log.error(errMsg);
				throw new IOException(errMsg);
			default:
				redirectsDone = true;
				break;
			}
			final String callbackToken = "oauth/callback?code="; 
			if (nextUrl.contains(callbackToken)) {
				oauthToken  = nextUrl.substring(nextUrl.lastIndexOf(callbackToken) + 1);
				log.debug("oauthToken: " + oauthToken);
			}
				
			
		} while (!redirectsDone);
		
		String loginResponse = response.body().string();
		log.debug("Login Response Body:" + loginResponse);
		
		// TODO: FIX THIS. Parse the /ui/main page to get the CSRF Token.
		Document uiMainDoc = Jsoup.parse(loginResponse);
		String globalContextSelector = "script[contains(., 'SLPT.globalContext.api')]";
		Elements globalContextScript = uiMainDoc.select(globalContextSelector);
		if (null == globalContextScript) {
			log.error("Failure extracting SLPT.globalContext.api with selector: " + globalContextSelector);
			return null;
		}
		
		String gcJsonBody = globalContextScript.html();
		log.debug("gcJsonBody:" + gcJsonBody);
		
		// TODO: Implement a class for this:
//		apiSlptGlobals = gson.fromJson(jsonBody, UiSailpointGlobals.class);
//		this.setApiGatewayUrl(apiSlptGlobals.getApi().getBaseUrl());
//		log.debug("API URL:" + this.getApiGatewayUrl());
		
		loginSequenceDuration = System.currentTimeMillis() - loginSequenceStartTime;
		log.debug("Login sequence completed in " + loginSequenceDuration + " msecs.");
		
		// TODO Parse out user interface version information from the HTTP:
		// This might be useful for internal debugging later.
		/**
	Built on: 2018/07/03 10:34:37
	Built by: Jenkins Pipeline
	Built Number: build103
	Built Target: prod
	Git branch: master
	Git commit: cd3c37a34516c35254e5eb13d37f8b93e6260480 
		 */
		return this;
	}
	
	@Override
	public void close() {
		
		// TODO: Use a common client builder that includes user-agents, etc.
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		OkHttpUtils.applyTimeoutSettings(clientBuilder);
		OkHttpUtils.applyLoggingInterceptors(clientBuilder);
		clientBuilder.cookieJar(new JavaNetCookieJar(cookieManager));
			
		OkHttpClient client = clientBuilder.build();
		
		long logoutStart = 0;
		long logoutEnd = 0;
		String uiUrl = getUserInterfaceUrl() + URL_LOGOUT;
		Response response;
		try {
			logoutStart = System.currentTimeMillis();
			response = doGet(uiUrl, client, null, null);
			logoutEnd = System.currentTimeMillis();
			if (log.isTraceEnabled()) {
				String respHtml = response.body().string();
				log.trace("respString: " + respHtml);
			}
		} catch (IOException e) {
			log.error("Failure while calling " + uiUrl, e);
			return;
		}
		
		// Handle or swallow various response / error conditions.
		switch (response.code()) {
		default:
			String defMsg = response.code() + " while GET'ing " + uiUrl + " - HTTP communication error."; 
			log.error(defMsg);			
		case 403:
			String errMsg = "403 while GET'ing " + uiUrl + " - Invalid client regional IP or VPN disconnected?"; 
			log.error(errMsg);
		case 200:
			// Successful base case.
			break;
		}
		
		logoutSequenceDuration = logoutEnd - logoutStart;
		log.debug("Logout processed in " + logoutSequenceDuration  + " msecs.");

		return;
		
	}

}
