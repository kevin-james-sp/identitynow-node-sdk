package sailpoint.services.idn.session;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.object.UiKbaQuestion;
import sailpoint.services.idn.sdk.object.UiLoginGetResponse;
import sailpoint.services.idn.sdk.object.UiSailpointGlobals;
import sailpoint.services.idn.sdk.object.UiSessionToken;
import sailpoint.services.idn.sdk.object.UiStrongAuthMethod;
import sailpoint.services.idn.sdk.object.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	// Max token age: 5 minutes in milliseconds.
	public static final long MAX_JWT_TOKEN_AGE = 300000;
	
	public String ssoUrl = null;
	public String ccSessionId = null;
	public String csrfToken = null;
	public String oauthToken = null;
	
	// Durations for performance analysis.
	public long loginSequenceDuration = 0;
	public long logoutSequenceDuration = 0; 
	public long lastTokenUpdate = 0;
	
	protected CookieManager cookieManager = new CookieManager();
	
	// Two clients used to talk to different edge interfaces of IdentityNow.
	protected OkHttpClient userInterfaceClient = null;
	protected OkHttpClient apiGatewayClient = null;
	
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
		// There is an ambiguiity here: Do we want the CC client or the API GW client.
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
	 * @return
	 */	
	public static String applySaltedHash(String user, String valueToHash) {
		String preHashPayload = valueToHash + sha256Hash(user.toLowerCase());
		log.debug("preHashPayload: " + preHashPayload);
		String completeHashPayload = sha256Hash(preHashPayload);
		log.debug("completeHashPayload: " + completeHashPayload);
		return completeHashPayload;		
	}

	private static String encryptPayload(String username, String password, String givenPublicKey) {

		String encPassword = password;

		if ( (null == givenPublicKey) || (0 == givenPublicKey.length()) ) {
			encPassword = encodeSHA256String( encPassword + encodeSHA256String(username) );
		} else {

			String cert = givenPublicKey;
			cert = cert.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");//.replace("\r","").replace("\n","")
			byte[] decodedBytes;
			try {
				decodedBytes = Base64.getDecoder().decode(cert.getBytes("UTF-8"));
				X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedBytes);
				KeyFactory kf = KeyFactory.getInstance("RSA");
				PublicKey pk = kf.generatePublic(publicKeySpec);

				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, pk);
				encPassword = new String(Base64.getEncoder().encode(cipher.doFinal(encPassword.getBytes())), "UTF-8");

			} catch (UnsupportedEncodingException e) {
				log.error("An UnsupportedEncodingException has occurred.", e);
			} catch (NoSuchAlgorithmException e) {
				log.error("An NoSuchAlgorithmException has occurred.", e);
			} catch (InvalidKeySpecException e) {
				log.error("An InvalidKeySpecException has occurred.", e);
			} catch (NoSuchPaddingException e) {
				log.error("An NoSuchPaddingException has occurred.", e);
			} catch (InvalidKeyException e) {
				log.error("An InvalidKeyException has occurred.", e);
			} catch (IllegalBlockSizeException e) {
				log.error("An IllegalBlockSizeException has occurred.", e);
			} catch (BadPaddingException e) {
				log.error("An BadPaddingException has occurred.", e);
			}

		}

		return encPassword;

	}

	public static String encodeSHA256String(String string) {
		return encodeSHA256String(string, null);
	}

	public static String encodeSHA256String(String string, String salt) {
		String combinedStr = string;
		if (null != salt) {
			combinedStr = combinedStr + salt;
		}
		String returnString = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(combinedStr.getBytes("UTF-8"));
			returnString = DatatypeConverter.printHexBinary((md).digest()).toLowerCase();

		} catch (Exception ex) {
			log.error("An error has occurred encoding password.", ex);
		}
		return returnString;
	}

	/**
	 * Return an OkHttpClient for use in calling into the API Gateway.
	 * The UI makes some (nay most?) of its API lookups via the API Gateway now.
	 * The API Gateway clients do _not_ utilize cookies the way the UI clients do.
	 * 
	 * This is a singleton; the client is not reconstructed every call, see: 
	 *    https://github.com/square/okhttp/issues/2636
	 *     
	 * @return
	 */
	public OkHttpClient getApiGatewayOkClient () {
		
		if (null != apiGatewayClient) return apiGatewayClient;
		
		OkHttpClient.Builder apiGwClientBuilder = new OkHttpClient.Builder();
		OkHttpUtils.applyTimeoutSettings(apiGwClientBuilder);
		OkHttpUtils.applyLoggingInterceptors(apiGwClientBuilder);
		apiGwClientBuilder.cookieJar(new JavaNetCookieJar(new CookieManager()));
		apiGatewayClient = apiGwClientBuilder.build();
		
		return apiGatewayClient;
	}
	
	/**
	 * Return an OkHttpClient for use in calling into the IdentityNow user interface.
	 * This supports traditional "v0" and "v1" API calls based on cookies, CSRF token
	 * and CCSESSIONID.   The cookie store is declared at the UserInterfaceSession 
	 * class instance and is shared by all calls made to the UI.
	 * 
	 * This is a singleton; the client is not reconstructed every call, see: 
	 *    https://github.com/square/okhttp/issues/2636
	 *    
	 * @return
	 */
	public OkHttpClient getUserInterfaceOkClient () {
		
		if (null != userInterfaceClient) return userInterfaceClient;
		
		OkHttpClient.Builder uiClientBuilder = new OkHttpClient.Builder();
		OkHttpUtils.applyTimeoutSettings(uiClientBuilder);
		OkHttpUtils.applyLoggingInterceptors(uiClientBuilder);
		uiClientBuilder.cookieJar(new JavaNetCookieJar(cookieManager));
		userInterfaceClient = uiClientBuilder.build();
		
		return userInterfaceClient;
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
		// 2. OPTIONS to API Gateway host to /cc/login/get URL suffix.
		// 3. POST API Gateway host /login/get   -- pulls back specific properties for the user logging in (hash vs. key password, etc).
		// 4. POST ${SSO Path}  -- makes a POST to the SSO (OpenAM/etc) login service with credentials.
		// 5. Follow the redirects from the SSO login.  
		//    This usually takes the browser through /ui/ then to /oauth/authorize? and /oauth/callback?
		//    Then finally to /ui and then /main
		// 6. Check for KBA map and if present do strong Auth-N.
		// 7. Check for API credentials and if present then do OAuth token for session.
		
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
		response.close();
		
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
		
		// STEP 2: Make an OPTIONS call to the API Gateway URL.
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

		//Build the options URLw
		String optionsUrl = getApiGatewayUrl() + "/cc/" + URL_LOGIN_GET;
		
		String apiGwHostHeader = getApiGatewayUrl();
		if (apiGwHostHeader.contains("//")) {
			apiGwHostHeader = apiGwHostHeader.split("\\/\\/")[1];
			apiGwHostHeader = apiGwHostHeader.split("\\/")[0];
		}
		
		Headers optionsRequestHeaders = new Headers.Builder()
			.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.add("Accept-Encodign", "identity")
			.add("Accept-Language", "en-US,en;q=0.5")
			.add("Access-Control-Request-Headers", "content-type")
			.add("Access-Control-Request-Method", "POST")
			.add("Host", apiGwHostHeader)
			.add("Origin", getUserInterfaceUrl())
			.add("User-Agent", OkHttpUtils.getUserAgent())
			.build();
		
		Request optionsRequest = new Request.Builder()
			.url(optionsUrl)
			.headers(optionsRequestHeaders)
			.method("OPTIONS", null)
			.build();
		
		Response optionsResponse = apiGwClient.newCall(optionsRequest).execute();
		if (!optionsResponse.isSuccessful()) {
			String errMsg = optionsResponse.code() + " in OPTIONS call to " + optionsUrl + " - Wrong API Gateway URL?"; 
			log.error(errMsg);
			throw new IOException(errMsg);
		}
		
		if (log.isDebugEnabled()) {
			String acam = optionsResponse.header("access-control-allow-methods").toString();
			log.debug("access-control-allow-methods: " + acam);
		}
		
		// STEP 3: Make a POST to /login/get to get the properties for the user.
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
		
		// STEP 4: Make a POST to the SSO path for the org.
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

		RequestBody formBody = null;
		String IDToken2 = null;
		String publicKey = null;
		if(apiLoginGetResponse.getApiAuth().getEncryptionType().equals("pki")){
			IDToken2 = encryptPayload(creds.getOrgUser(), creds.getOrgPass(), apiLoginGetResponse.getApiAuth().getPublicKey());
			publicKey = apiLoginGetResponse.getApiAuth().getPublicKey();
		} else{
			IDToken2 = applySaltedHash(creds.getOrgUser(), creds.getOrgPass());
		}
		
		FormBody.Builder formBodyBuilder = new FormBody.Builder()
				.add("encryption", apiLoginGetResponse.getApiAuth().getEncryptionType())
				.add("service",    apiLoginGetResponse.getApiAuth().getService())
				.add("IDToken1",   creds.getOrgUser())
				.add("IDToken2",   IDToken2)
				.add("realm",      apiSlptGlobals.getOrgScriptName())
				.add("goto",       creds.getUserIntUrl() + URL_UI)
				.add("gotoOnFail", onFailUrl)
				.add("openam.session.persist_am_cookie", "true");
		
		if (null != publicKey) { 
			formBodyBuilder.add("publicKey",  publicKey);
		}
		
		formBody = formBodyBuilder.build();
		
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
		
		// Parse the /ui/main page to get the CSRF Token.
		String csrfTokenRegex = "SLPT.globalContext.csrf\\s=\\s'(\\w+)'";
		Pattern p = Pattern.compile(csrfTokenRegex);
		Matcher m = p.matcher(loginResponse);
		if (m.find()) {
			csrfToken = m.group(1);
			log.debug("Parsed CSRF Token: " + csrfToken);
		} else {
			if (log.isDebugEnabled()) {
				log.warn("Failed to parse CSRF token from 'SLPT.globalContext.csrf' for CCSESSIONID: " + ccSessionId + " HTML was: " + loginResponse);
			} else {
				log.warn("Failed to parse CSRF token from 'SLPT.globalContext.csrf' for CCSESSIONID: " + ccSessionId);
			}
			
			return null;
		}
		
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
		
		// Allow the environment to turn off the /ui/session call for testing.
		if (Boolean.parseBoolean(System.getProperty("skipUiSessionCall", "false"))) {
			log.debug("Skipping /ui/session call by config request.");
		} else {
			getNewSessionToken();
		}
		
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
		
		// TODO: Make an API Gateway call to CC's api/user/get interface.
		
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
	
	/**
	 * Strongly authenticate the User Interface session by submitting answers to KBA questions.
	 * @param kbaAnswers
	 * @return the newly gotten session token.
	 */
	public String stronglyAuthenticate() {
		
		OkHttpClient apiGwClient = getApiGatewayOkClient(); 
		
		// This call lists the strong authentication methods.
		String getStrongAuthMethodsURL =  getApiGatewayUrl() + "/cc/api/user/getStrongAuthnMethods?_dc=" + System.currentTimeMillis();
		
		HashMap<String,String> apiHeadersMap = new HashMap<String,String>();
		apiHeadersMap.put("Authorization", "Bearer " + this.accessToken);
		
		Response response;
		String getStrongAuthMethodsJsonArray;
		try {
			response = doGet(getStrongAuthMethodsURL, apiGwClient, apiHeadersMap, null);
			getStrongAuthMethodsJsonArray = response.body().string();
			log.debug("getStrongAuthnMethods: " + getStrongAuthMethodsJsonArray);
		} catch (IOException e) {
			log.error("Failure while calling " + getStrongAuthMethodsURL, e);
			return null;
		}
		
		// Handle non-200 responses here!
		if (!response.isSuccessful()) {
			return null;
		}
		
		Gson gson = new Gson();
		
		Type listTypeStrongAuthn = new TypeToken<ArrayList<UiStrongAuthMethod>>(){}.getType();
		List<UiStrongAuthMethod> availableMethods = new Gson().fromJson(getStrongAuthMethodsJsonArray, listTypeStrongAuthn);
		
		// Traverse the list and ensure that a KBA type is available.
		boolean kbaTypeAvailable = false;
		for (UiStrongAuthMethod uisam : availableMethods) {
			if ("KBA".equals(uisam.getStrongAuthType())) {
				kbaTypeAvailable = true;
			}
		}
		
		if (!kbaTypeAvailable) {
			log.error("No KBA strong authentication is available for this user.");
			return null;
		}
		
		String getChlngQsURL = getApiGatewayUrl() + "/cc/api/challenge/list?allLanguages=false&_dc=" + System.currentTimeMillis();
		
		// Pull back the list of challenge questions available for the user.
		String apiChallengeListJsonArray;
		try {
			response = doGet(getChlngQsURL, apiGwClient, apiHeadersMap, null);
			apiChallengeListJsonArray = response.body().string();
			log.debug("api/challenge/list: " + apiChallengeListJsonArray);
		} catch (IOException e) {
			log.error("Failure while calling " + getChlngQsURL, e);
			return null;
		}
		
		// Handle non-200 responses here!
		if (!response.isSuccessful()) {
			return null;
		}
		
		Type listTypeChallengeQuestion = new TypeToken<ArrayList<UiKbaQuestion>>(){}.getType();
		List<UiKbaQuestion> availableKbaQuestions = new Gson().fromJson(apiChallengeListJsonArray, listTypeChallengeQuestion);
		
		// Find the KBA questions that the user has an answer specified for.
		ArrayList<UiKbaQuestion> answeredKbaQuestions = new ArrayList<UiKbaQuestion>(); 
		for (UiKbaQuestion thisKbaQ : availableKbaQuestions) {
			if (thisKbaQ.isHasAnswer()) {
				answeredKbaQuestions.add(thisKbaQ);
			}
		}
		
		log.debug("User has " + answeredKbaQuestions.size() + " answered KBA questions.");
		
		// The user interface presents N strongAuthn questions to answer.  A user typically
		// must answer a sub-set of these to strongly authenticate.  Say there are 5 questions
		// with an answer provided and 3 answers must be sumbitted for the strong-auth to go 
		// through. 
		// This next call gets the number that has to be answered.
		String userGetUrl = getApiGatewayUrl() + "/cc/api/user/get?_dc=" + System.currentTimeMillis();
		String userGetJson;
		try {
			response = doGet(userGetUrl, apiGwClient, apiHeadersMap, null);
			userGetJson = response.body().string();
			log.debug("/api/user/get: " + userGetJson);
		} catch (IOException e) {
			log.error("Failure while calling " + userGetUrl, e);
			return null;
		}
		
		User user = gson.fromJson(userGetJson, User.class);
		log.debug("kbaReqForAuthn: " + user.getKbaReqForAuthn());
		
		// Strong Authentication payloads are sent as a JSON array of hashed
		// toLowerCase() answers paired with the ID string of the KBA question 
		// they answer. Example:
		// [
		//    {"id":"2163","answer":"a5f058b4a8882c6f5704cc9fae279cbb348612a1a3bb81581931fbdc1d5de3f1"},
		//    {"id":"2164","answer":"a5f058b4a8882c6f5704cc9fae279cbb348612a1a3bb81581931fbdc1d5de3f1"}
		// ]
		// We put this in a "scrubbedQuestions" list that only has IDs and Answer strings.
		
		// Apply the user's answer and submit the strong authentication back up to the UI.
		ClientCredentials creds = getCredentials();
		ArrayList<UiKbaQuestion> scrubbedQuestions = new ArrayList<UiKbaQuestion>();
		for (UiKbaQuestion kbaQuestion : answeredKbaQuestions) {
			String answer = creds.getKbaAnswer(kbaQuestion.getText());
			if (null != answer) {
				log.debug("Setting user answer to [" + kbaQuestion.getText() + "] ==> " + answer);
				
				String hashedStrongAuthnCredential = sha256Hash(answer.toLowerCase());
				
				UiKbaQuestion scrubbedQ = new UiKbaQuestion();
				scrubbedQ.setId(kbaQuestion.getId());
				scrubbedQ.setAnswer(hashedStrongAuthnCredential);
				scrubbedQ.setHasAnswer(true);
				scrubbedQuestions.add(scrubbedQ);
			}
		}

		// Build out JSON string here:
		String scrubbedQJsonArrayString = gson.toJson(scrubbedQuestions);
		log.debug("scrubbedQJsonArrayString: ", scrubbedQJsonArrayString);
		
		// Submit the Strong Authn payload.  Note that this call is made directly
		// to the user interface URL and not to the API Gateway's URL.  This means we
		// have to use the cookie manager from the UI interaction.
		
		OkHttpClient uiClient = getUserInterfaceOkClient();
		
		HashMap<String,String> uiHeadersMap = new HashMap<String,String>();
		uiHeadersMap.put("X-CSRF-Token", this.csrfToken);
		
		String apiStronAuthn = getUserInterfaceUrl() + "api/user/strongAuthn";
		String strongAuthnResponseStr;
		try {
			response = doPost(apiStronAuthn, scrubbedQJsonArrayString, uiClient, uiHeadersMap, null);
			strongAuthnResponseStr = response.body().string();
			log.debug("api/user/strongAuthn: " + strongAuthnResponseStr);
		} catch (IOException e) {
			log.error("Failure while calling " + apiStronAuthn, e);
			return null;
		}
		
		// Handle non-200 response.
		if (!response.isSuccessful()) {
			return null;
		}
		
		// Get a new session token to reflect the strongly authenticated status of the session.
		if (Boolean.parseBoolean(System.getProperty("skipUiSessionCall", "false"))) {
			log.debug("Skipping /ui/session call by config request.");
			return accessToken;
		} 
		
		return getNewSessionToken();
		
	}
	
	/**
	 * Retrieves a new JWT session token for user interface session.  This is used by
	 * user interface sessions to call into the API gateway.
	 * 
	 * @return
	 */
	public String getNewSessionToken() {
		
		OkHttpClient uiClient = getUserInterfaceOkClient();
		
		String uiSessionUrl = getUserInterfaceUrl() + "ui/session";
		
		Response response;
		String uiSessionResponseJson;
		try {
			response = doGet(uiSessionUrl, uiClient, null, null);
			uiSessionResponseJson = response.body().string();
			log.debug("UiSessionToken: " + uiSessionResponseJson);
		} catch (IOException e) {
			log.error("Failure while calling " + uiSessionUrl, e);
			return null;
		}
		
		// TODO: Handle non-200 responses here!
		if (!response.isSuccessful()) {
			return null;
		}
		
		Gson gson = new Gson();
		
		UiSessionToken uiSessToken = gson.fromJson(uiSessionResponseJson, UiSessionToken.class);
		
		lastTokenUpdate = System.currentTimeMillis();
		this.csrfToken = uiSessToken.getCsrfToken();
		this.oauthToken = uiSessToken.getAccessToken();
		this.accessToken = uiSessToken.getAccessToken();
		
		
		return uiSessToken.getAccessToken();
	}
	
	// Update the JWT token used for this session if we get close to expiration.
	// Return true of a new token was requested/retrieved, false if the same one remains.
	public boolean checkTokenExpiration() {
		
		long tokenAge = System.currentTimeMillis() - lastTokenUpdate;
		if (tokenAge > MAX_JWT_TOKEN_AGE) {
			getNewSessionToken();
			return true;
		}
		
		return false;
		
	}
	
	public String doApiGet (String apiUrlSuffix) {
		
		String apiUrl = getApiGatewayUrl() + apiUrlSuffix;
		
		HashMap<String,String> apiHeadersMap = new HashMap<String,String>();
		apiHeadersMap.put("Authorization", "Bearer " + this.accessToken);
		// apiHeadersMap.put("User-Agent", OkHttpUtils.getUserAgent());
		
		try (Response response = doGet(apiUrl, getApiGatewayOkClient(), apiHeadersMap, null)) {
			if (!response.isSuccessful()) {
				log.error(response.code() + " while calling " + apiUrl);
			}
			String responseJson = response.body().string();
			log.debug(apiUrlSuffix + ": " + responseJson);
			return responseJson;
		} catch (IOException e) {
			log.error("Failure while calling " + apiUrl, e);
			return null;
		}
		
	}

}
