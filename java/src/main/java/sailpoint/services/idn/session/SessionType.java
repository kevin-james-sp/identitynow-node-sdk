package sailpoint.services.idn.session;

/**
 * IdentityNow provides a number of ways to access data by interacting with
 * its servers.  Moving forward the product will evolve to making all use cases
 * available through the API Gateway host for an organization. 
 * 
 * In the interim there are multiple other ways to call into an IdentityNow org
 * that include emulating a user's session as it is established by the browser.
 * 
 * @author adam.hampton
 *
 */
public enum SessionType {
	
	/**
	 * API Only session types are established through the API Gateway host for
	 * the IdentityNow organization.  They require the presence of a Client Id
	 * and Client Secret (aka API Key and API Secret) for accessing the org.
	 * 
	 * API Calls made with this session use a JWT Token when accessing the API
	 * gateway.  These credentials can also be used to access the UI servers 
	 * directly via Authorization Bearer type legacy API calls.
	 * 
	 */
	SESSION_TYPE_API_ONLY,
	
	/**
	 * API with User credentials in context to create an OAuth token for the 
	 * session.
	 */	
	SESSION_TYPE_API_WITH_USER,
	
	/**
	 * A basic user interface session for a user that is not strongly authenticated.
	 */
	SESSION_TYPE_UI_USER_BASIC,
	
	/**
	 * A user session that has completed its strong-authentication action, like 
	 * getting an SMS text or keying in Knowledge Based Authentication security 
	 * question answers.
	 */
	SESSION_TYPE_UI_USER_STRONG_AUTHN
	
}
