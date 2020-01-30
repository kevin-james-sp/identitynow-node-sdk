package sailpoint.services.idn.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A container for all of the relevant strings and credentials information required 
 * to communicate with an IdentityNow organization through an API Gateway. 
 * @author adam.hampton
 *
 */
public class ClientCredentials extends ConcurrentHashMap<String,String> {
	
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
	public static final String PERS_ACC_TKN  = "persAccToken"; // Personal access token ID for use-associated API calls.
	public static final String PERS_ACC_SCR  = "persAccTkScr"; // Personal access token Secret for use-associated API calls.
	
	// Maintain a mapping of Knowledge Based Authentication question substring to answer text.
	// Environment parameters like: kbaQ_1, kbaA_1, kbaQ_2, kbaA_2, etc.
	private ArrayList<String> kbaQTextList = new ArrayList<String>();
	private ConcurrentHashMap<String,String> kbaQtoAMap = new ConcurrentHashMap<String,String>();
	
	// We want to alter the entire ClientCredentials class to 
	// extend ConcurrentHashMap eventually.  That means no setting
	// values to NULL here below, so these will have to alter the way 
	// they work.
	private void setFieldWithNull (String fieldKey, String newValue) {
		if (null == newValue) {
			this.remove(fieldKey);
		} else {
			this.put(fieldKey, newValue.trim());
		}
	}
	
	/**
	 * Constructor passing all known properties for an org.
	 * @param userIntUrl    - The user interface URL.  Specifiable to support vanity URLs.
	 * @param orgScriptName    - The script name of the organization.  A 16 or fewer character unique string for the Org
	 * @param orgUser       - A user to login to the interface of the organization.
	 * @param orgPass       - A password to login to the interface of the organization.
	 * @param clientId      - A Client ID for API based interactions to the org.
	 * @param clientSecret  - A Client Secret for API based interactions to the org.
	 */
	public ClientCredentials (
			String userIntUrl, String orgScriptName, String orgUser, 
			String orgPass, String clientId, String clientSecret) {
		super();
		setFieldWithNull(USERINT_URL,    userIntUrl.trim()    );
		setFieldWithNull(ORG_NAME,       orgScriptName.trim() );
		setFieldWithNull(ORG_USER,       orgUser.trim()       );
		setFieldWithNull(ORG_PASS,       orgPass.trim()       );
		setFieldWithNull(CLIENT_ID,      clientId.trim()      );
		setFieldWithNull(CLIENT_SECRET,  clientSecret.trim()  );
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
	public String getPersAccTkn()   { return this.get(PERS_ACC_TKN);  }
	public String getPersAccScr()   { return this.get(PERS_ACC_SCR);  }
	
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
	public void setGatewayUrl(String arg)   { setFieldWithNull(GATEWAY_URL,    arg); }
	public void setUserIntUrl(String arg)   { setFieldWithNull(USERINT_URL,    arg); }
	public void setOrgName(String arg)      { setFieldWithNull(ORG_NAME,       arg); }
	public void setOrgUser(String arg)      { setFieldWithNull(ORG_USER,       arg); }
	public void setOrgPass(String arg)      { setFieldWithNull(ORG_PASS,       arg); }
	public void setOrgPassHash(String arg)  { setFieldWithNull(ORG_PASS_HASH,  arg); }
	public void setClientId(String arg)     { setFieldWithNull(CLIENT_ID,      arg); }
	public void setClientSecret(String arg) { setFieldWithNull(CLIENT_SECRET,  arg); }
	public void setOAuthToken(String arg)   { setFieldWithNull(OAUTH_TOKEN,    arg); }
	public void setJWTToken(String arg)     { setFieldWithNull(JWT_TOKEN,      arg); }
	public void setExpiresIn(String arg)    { setFieldWithNull(EXPIRES_IN,     arg); }
	public void setKbaDefault(String arg)   { setFieldWithNull(KBA_DEFAULT,    arg); }
	public void setPersAccTkn(String arg)   { setFieldWithNull(PERS_ACC_TKN,   arg); }
	public void setPersAccScr(String arg)   { setFieldWithNull(PERS_ACC_SCR,   arg); }
	
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
			if ( 
					k.toLowerCase().contains(questionText.toLowerCase()) ||
					questionText.toLowerCase().contains(k.toLowerCase())
			) {
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
	
	/**
	 * Tell the caller if the credentials set has a Personal Access token available.
	 * A personal access token can invoke APIs with a user's access model in context.
	 * @return
	 */
	public boolean hasPersonalAccessToken() {
		if (null == getPersAccTkn()) return false;
		if (null == getPersAccScr()) return false;
		return true;
	}

}
