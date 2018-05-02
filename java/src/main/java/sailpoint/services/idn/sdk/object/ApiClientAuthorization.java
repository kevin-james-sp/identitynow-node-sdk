package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * Response returned by an IdentityNow API Gateway server when requesting a JWT 
 * token be created and authorized for the presented Client ID / Client Secret
 * API credentials.
 * 
 * @author adam.hampton
 *
 */
public class ApiClientAuthorization {
	
	/*
	 {"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwb2QiOiJkZXYwMS11c2Vhc3QxIiwib3JnIjoicGVyZmxhYi0wOTA3MjE0MCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1MjUzMzQyMjAsImF1dGhvcml0aWVzIjpbIkFQSSJdLCJqdGkiOiIzNjUwYjlkNS0wOGU1LTRiNTQtYjFmOS02ZTliYTk0N2FhYWQiLCJjbGllbnRfaWQiOiJmZnpmWk5XQnQwZEY3Q0ljIn0.dR8MK6Wli3ePLYYFgdW_Qr8XhLVZJoJboFmFBMc4h5s",
	 "token_type":"bearer",
	 "expires_in":43199,
	 "scope":"read write",
	 "pod":"dev01-useast1",
	 "org":"perflab-09072140",
	 "jti":"3650b9d5-08e5-4b54-b1f9-6e9ba947aaad"
	 }
	 */
	
	@SerializedName("access_token")
	public String accessToken;
	
	@SerializedName("token_type")
	public String tokenType;
	
	@SerializedName("expires_in")
	public int expiresIn;
	
	@SerializedName("scope")
	public String scope;
	
	@SerializedName("pod")
	public String pod;
	
	@SerializedName("org")
	public String org;
	
	@SerializedName("jti")
	public String jti;
	
	public String getAccessToken () {
		return accessToken;	
	}
	
	public int getExpiresIn() {
		return expiresIn;
	}
	
	public String getOrg() { 
		return org;
	}
	
	public String getPod() {
		return pod;
	}

}
