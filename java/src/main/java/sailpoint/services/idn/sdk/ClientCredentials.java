package sailpoint.services.idn.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A container for all of the relevant strings and credentials information required 
 * to communicate with an IdentityNow organization through an API Gateway. 
 * @author adam.hampton
 *
 */
public class ClientCredentials extends HashMap<String,String> {
	
	private static final long serialVersionUID = -1L;
	
	public static final String GATEWAY_URL   = "gatewayUrl";   // API Gateway URL for the Org.
	public static final String USERINT_URL   = "userIntUrl";   // User interface URL for the Org.
	public static final String ORG_NAME      = "orgName";      // The script name of the Org.
	public static final String ORG_USER      = "orgUser";      // The user account to authenticate with.
	public static final String ORG_PASS      = "orgPass";      // The password of the user account to authenticate with.
	public static final String ORG_PASS_HASH = "orgPass";      // The pre-hashed password of the user account to authenticate with.
	public static final String CLIENT_ID     = "clientId";     // The Client ID / API Key to communicate to the org with.
	public static final String CLIENT_SECRET = "clientSecret"; // The Client Secret / AP Secret to communicate to the org with.
	public static final String OAUTH_TOKEN   = "oAuthToken";   // The OAuth token created for the user's session in the UI.
	public static final String JWT_TOKEN     = "jwtToken";     // The JWT Token created for the client when authenticated.
	public static final String EXPIRES_IN    = "expiresIn";    // The expiration time for the JWT token.
	public static final String KBA_DEFAULT   = "kbaDefault";   // The default answer to Knowledge Based Authentication questions for strong auth-n.
	
	// Maintain a mapping of Knowledge Based Authentication question substring to answer text.
	// Environment parameters like: kbaQ_1, kbaA_1, kbaQ_2, kbaA_2, etc.
	private ArrayList<String> kbaQTextList = new ArrayList<String>();
	private HashMap<String,String> kbaQtoAMap = new HashMap<String,String>();
	
	/**
	 * Constructor passing all known properties for an org.
	 * @param userIntUrl    - The user interface URL.  Specifiable to support vanity URLs.
	 * @param ScriptName    - The script name of the organization.  A 16 or fewer character unique string for the Org
	 * @param orgUser       - A user to login to the interface of the organization.
	 * @param orgPass       - A password to login to the interface of the organization.
	 * @param clientId      - A Client ID for API based interactions to the org.
	 * @param clientSecret  - A Client Secret for API based interactions to the org.
	 */
	public ClientCredentials (
			String userIntUrl, String orgScriptName, String orgUser, 
			String orgPass, String clientId, String clientSecret) {
		super();
		this.put(USERINT_URL,    (null != userIntUrl)    ? userIntUrl.trim()    : null);
		this.put(ORG_NAME,       (null != orgScriptName) ? orgScriptName.trim() : null);
		this.put(ORG_USER,       (null != orgUser)       ? orgUser.trim()       : null);
		this.put(ORG_PASS,       (null != orgPass)       ? orgPass.trim()       : null);
		this.put(CLIENT_ID,      (null != clientId)      ? clientId.trim()      : null);
		this.put(CLIENT_SECRET,  (null != clientSecret)  ? clientSecret.trim()  : null);
	}
	
	/**
	 * Constructor allowing the caller to set the properties after construction.
	 */
	public ClientCredentials () {
		super();
	}
	
	// Cookie-cutter get()-ers that all pass the field through verbatim. 
	
	public String getUserIntUrl()   { return this.get(USERINT_URL);   }
	public String getOrgUser()      { return this.get(ORG_USER);      }
	public String getOrgPass()      { return this.get(ORG_PASS);      }
	public String getOrgPassHash()  { return this.get(ORG_PASS_HASH); }
	public String getClientId()     { return this.get(CLIENT_ID);     }
	public String getClientSecret() { return this.get(CLIENT_SECRET); }	
	public String getOAuthToken()   { return this.get(OAUTH_TOKEN);   }
	public String getJWTToken()     { return this.get(JWT_TOKEN);     }
	public String getExpiresIn()    { return this.get(EXPIRES_IN);    }
	public String getKbaDefault()   { return this.get(KBA_DEFAULT);   }
	
	/**
	 * Returns the org's script name to the caller.  
	 * This is a 16 or fewer character string that uniquely describes the Org.
	 * If not passed in by at the time of ClientCredentials construction then 
	 * this can be derived by a number of methods, including:
	 *  - Parsing the API Gateway URL for any kind of org.
	 *  - Parsing the user interface URL for non-vanity orgs.
	 * This get()-er attempts to derive it of no orgName (aka org script name)
	 * is provided at the time of credentials construction.
	 * @return
	 */
	public String getOrgName() {
		String orgName = this.get(ORG_NAME);
		if ((null != orgName) && (0 != orgName.length())) return orgName;
		if (null != getGatewayUrl()) {
			// Gateway URLs are of the format:
			// "https://${orgScriptName}.api.cloud.sailpoint.com"
			Pattern p = Pattern.compile("https:\\/\\/(\\S+).");
			Matcher m = p.matcher(getGatewayUrl());
			if (m.find()) {
				return m.group(1);
			}
		}
		return null;
	}
	
	// Cookie-cutter set()-ers that all trim any strings passed in.
	
	public void setGatewayUrl(String arg)   { this.put(GATEWAY_URL,   (null != arg ? arg.trim() : null) ); }
	public void setUserIntUrl(String arg)   { this.put(USERINT_URL,   (null != arg ? arg.trim() : null) ); }
	public void setOrgName(String arg)      { this.put(ORG_NAME,      (null != arg ? arg.trim() : null) ); }
	public void setOrgUser(String arg)      { this.put(ORG_USER,      (null != arg ? arg.trim() : null) ); }
	public void setOrgPass(String arg)      { this.put(ORG_PASS,      (null != arg ? arg.trim() : null) ); }
	public void setOrgPassHash(String arg)  { this.put(ORG_PASS_HASH, (null != arg ? arg.trim() : null) ); }
	public void setClientId(String arg)     { this.put(CLIENT_ID,     (null != arg ? arg.trim() : null) ); }
	public void setClientSecret(String arg) { this.put(CLIENT_SECRET, (null != arg ? arg.trim() : null) ); }
	public void setOAuthToken(String arg)   { this.put(OAUTH_TOKEN,   (null != arg ? arg.trim() : null) ); }
	public void setJWTToken(String arg)     { this.put(JWT_TOKEN,     (null != arg ? arg.trim() : null) ); }
	public void setExpiresIn(String arg)    { this.put(EXPIRES_IN,    (null != arg ? arg.trim() : null) ); }
	public void setKbaDefault(String arg)   { this.put(KBA_DEFAULT,   (null != arg ? arg.trim() : null) ); }
	
	/** 
	 * The API Gateway URL for IdentityNow organizations is strictly derived 
	 * from the script name for the organization.  This is different from the UI
	 * URL, which can be configured with vanity URLs for specific Orgs.
	 * We allow the client to specify it for testing purposes, but it is not strictly 
	 * necessary that we specify it in the environment.  UI Sessions can derive it
	 * as well based on the interactions with the post 2018Q2 user interface releases.
	 */
	public String getGatewayUrl()   {
		if (null != this.get(GATEWAY_URL)) {
			return this.get(GATEWAY_URL);
		}
		if (null != getOrgName()) {
			return "https://" + getOrgName() + ".api.cloud.sailpoint.com";
		}
		return null;
	}
	
	
	/**
	 * Retrieve the answer for a given KBA question.  Checks the hash map first,
	 * if match is found then that is returned, checks defaults second and if
	 * one is found then that is returned, otherwise null is returned. 
	 * @param questionText
	 * @return
	 */
	public String getKbaAnswer (String questionText) {
		
		// Iterate through the Question text keys to find a match.
		for (String k : kbaQtoAMap.keySet()) {
			if (k.toLowerCase().contains(questionText.toLowerCase())) {
				String a = kbaQtoAMap.get(k);
				if ((null != a) && (0 != a.length())) {
					return a;
				}
			}
		}
		
		// No question text keys mathched, see if there is a default to return.
		String defaultKba = getKbaDefault();
		if ((null != defaultKba) && (0 != defaultKba.trim().length())) {
			return defaultKba;
		}
		
		// Base case: No KBA question to answer mapping found and o default specified,
		// return null!	
		return null;
	}
	
	/**
	 * Maps an answer to a given KBA question for this user.  
	 * Over-writes any in RAM previous value that was assigned.
	 * @param questionText
	 * @param answerText
	 */
	public void setKbaAnswer (String questionText, String answerText) {
		if (!kbaQTextList.contains(questionText)) {
			kbaQTextList.add(questionText);
		}
		kbaQtoAMap.put(questionText.trim(), answerText.trim());
	}
	
	/**
	 * Return the list of populated KBA questions for the credentials set. 
	 * Note: This list _may_ be empty, but it should always be initialized.
	 * @return
	 */
	public List<String> getKbaQuestionTexts() {
		return kbaQTextList;
	}
	
	/**
	 * Tell the caller if the credentials set has a user's name and password.
	 * This allows the credentials to be used for creating a UI session. 
	 * @return
	 */
	public boolean hasUserCredentials() {
		if (null == getOrgUser()) return false;
		if (null == getOrgPass()) return false;
		return true;
	}
	
	/**
	 * Tell the caller if the credentials set has a user's name and password and
	 * the credentials set has at least a default KBA populated or some answers.
	 * This allows the credentials to be used for creating a UI session that has
	 * strongly authenticated to the user interface to do high-privilege actions.
	 * @return
	 */
	public boolean hasUserStrongCredentials() {
		if (!hasUserCredentials()) return false;
		if (null == getKbaDefault() && kbaQTextList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Tell the caller if the credentials set has API credentials in the form
	 * of a Client ID and Client Secret.  
	 * This allows the credentials to be used to interact with the API gateway
	 * as an administrator type interface, free of user context.
	 * @return
	 */
	public boolean hasApiCredentials() {
		if (null == getClientId()) return false;
		if (null == getClientSecret()) return false;
		return true;
		
	}

}
