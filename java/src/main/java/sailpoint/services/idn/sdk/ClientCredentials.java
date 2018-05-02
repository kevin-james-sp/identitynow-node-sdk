package sailpoint.services.idn.sdk;

import java.util.HashMap;

/**
 * A container for all of the relevant strings and credentials information required 
 * to communicate with an IdentityNow organization through an API Gateway. 
 * @author adam.hampton
 *
 */
public class ClientCredentials extends HashMap<String,String> {
	
	private static final long serialVersionUID = -1L;
	
	public static final String GATEWAY_URL   = "gatewayUrl";   // API Gateway URL for the Org.
	public static final String USERINT_URL   = "gatewayUrl";   // User interface URL for the Org.
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
	
	/*
	public static final String KBA_QTEXT_1   = "kbaQText1";    // The full question or unique substring of text from a KBA question.
	public static final String KBA_ATEXT_1   = "kbaAText1";    // The answer to a KBA question.
	*/
	
	// Maintain a mapping of Knowledge Based Authentication question substring to answer text.
	private HashMap<String,String> kbaQtoAMap = new HashMap<String,String>();
	
	/**
	 * Constructor passing all known properties for an org.
	 * @param gatewayUrl
	 * @param orgName
	 * @param orgUser
	 * @param orgPass
	 * @param clientId
	 * @param clientSecret
	 */
	public ClientCredentials (
			String gatewayUrl, String orgName, String orgUser, 
			String orgPass, String clientId, String clientSecret) {
		super();
		this.put(GATEWAY_URL, gatewayUrl);
		this.put(ORG_NAME, orgName);
		this.put(ORG_USER, orgUser);
		this.put(ORG_PASS, orgPass);
		this.put(CLIENT_ID, clientId);
		this.put(CLIENT_SECRET, clientSecret);		
	}
	
	/**
	 * Constructor allowing the caller to set the properties after construction.
	 */
	public ClientCredentials () {
		super();
	}
	
	public String getGatewayUrl()   { return this.get(GATEWAY_URL);   }
	public String getUserIntUrl()   { return this.get(USERINT_URL);   }
	public String getOrgName()      { return this.get(ORG_NAME);      }
	public String getOrgUser()      { return this.get(ORG_USER);      }
	public String getOrgPass()      { return this.get(ORG_PASS);      }
	public String getOrgPassHash()  { return this.get(ORG_PASS_HASH); }
	public String getClientId()     { return this.get(CLIENT_ID);     }
	public String getClientSecret() { return this.get(CLIENT_SECRET); }	
	public String getOAuthToken()   { return this.get(OAUTH_TOKEN);   }
	public String getJWTToken()     { return this.get(JWT_TOKEN);     }
	public String getExpiresIn()    { return this.get(EXPIRES_IN);    }
	public String getKbaDefault()   { return this.get(KBA_DEFAULT);   }
	
	public void setGatewayUrl(String arg)   { this.put(GATEWAY_URL,   arg.trim()); }
	public void setUserIntUrl(String arg)   { this.put(USERINT_URL,   arg.trim()); }
	public void setOrgName(String arg)      { this.put(ORG_NAME,      arg.trim()); }
	public void setOrgUser(String arg)      { this.put(ORG_USER,      arg.trim()); }
	public void setOrgPass(String arg)      { this.put(ORG_PASS,      arg.trim()); }
	public void setOrgPassHash(String arg)  { this.put(ORG_PASS_HASH, arg.trim()); }
	public void setClientId(String arg)     { this.put(CLIENT_ID,     arg.trim()); }
	public void setClientSecret(String arg) { this.put(CLIENT_SECRET, arg.trim()); }
	public void setOAuthToken(String arg)   { this.put(OAUTH_TOKEN,   arg.trim()); }
	public void setJWTToken(String arg)     { this.put(JWT_TOKEN,     arg.trim()); }
	public void setExpiresIn(String arg)    { this.put(EXPIRES_IN,    arg.trim()); }
	public void setKbaDefault(String arg)   { this.put(KBA_DEFAULT,   arg.trim()); }
	
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
		kbaQtoAMap.put(questionText.trim(), answerText.trim());
	}

}
