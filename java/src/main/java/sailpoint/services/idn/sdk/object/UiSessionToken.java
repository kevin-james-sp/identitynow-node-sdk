package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * This class of data is returned from calls into /ui/session like this:
 * 
 *    https://customer.identitynow.com/ui/session
 *    
 * This returns a payload including a JWT token that allows a user to call
 * into the API gateway to lookup various properties via the API.
 * @author adam.hampton
 *
 */
public class UiSessionToken {
	
	// Exmample JSON:
	/*
	 {
	  "authType":"OAuth2.0",
	  "baseUrl":"https://perflab-05191440.api.cloud.sailpoint.com",
	  "logoutUrl":"https://dev01-useast1.cloud.sailpoint.com/perflab-05191440/logout",
	  "accessToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwb2QiOiJkZXYwMS11c2Vhc3QxIiwic3Ryb25nX2F1dGhfc3VwcG9ydGVkIjp0cnVlLCJvcmciOiJwZXJmbGFiLTA1MTkxNDQwIiwidXNlcl9pZCI6Ijk5MDUwMjYiLCJpZGVudGl0eV9pZCI6ImZmODA4MTgxNWMyMjNkZDQwMTVjMjIzZjIxYTMwMzI5IiwidXNlcl9uYW1lIjoic3VwcG9ydCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJzdHJvbmdfYXV0aCI6ZmFsc2UsImV4cCI6MTUzMjQ4MzQ1NywiYXV0aG9yaXRpZXMiOlsiT1JHX0FETUlOIl0sImp0aSI6ImM5MzA4NDEwLWJjNTItNGM2Yi05YmM5LTM5ZjZlMmZlN2VlNyIsImNsaWVudF9pZCI6Ik1LekhZRHYwRUsyNkZRMGMifQ.Pnr-zMGuy99pfsNZt8rOfYBq1TjaqLJv-h-o-9o2HZE",
	  "refreshIn":581892,
	  "pollUrl":"https://dev01-useast1.cloud.sailpoint.com/perflab-05191440/ui/session",
	  "strongAuth":false,
	  "strongAuthUrl":"https://dev01-useast1.cloud.sailpoint.com/perflab-05191440/api/user/strongAuthn",
	  "csrfToken":"D4phoAy7VzuEZTLUam9PyhhlikpIq4Gi"
	  }
	 */
	
	@SerializedName("authType")
	public String authType;
	
	@SerializedName("baseUrl")
	public String baseUrl;
	
	@SerializedName("logoutUrl")
	public String logoutUrl;
	
	@SerializedName("accessToken")
	public String accessToken;
	
	@SerializedName("refreshIn")
	public int refreshIn;
	
	@SerializedName("pollUrl")
	public int pollUrl;
	
	@SerializedName("strongAuth")
	public boolean strongAuth;
	
	@SerializedName("strongAuthUrl")
	public String strongAuthUrl;
	
	@SerializedName("csrfToken")
	public String csrfToken;
	
	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getRefreshIn() {
		return refreshIn;
	}

	public void setRefreshIn(int refreshIn) {
		this.refreshIn = refreshIn;
	}

	public int getPollUrl() {
		return pollUrl;
	}

	public void setPollUrl(int pollUrl) {
		this.pollUrl = pollUrl;
	}

	public boolean isStrongAuth() {
		return strongAuth;
	}

	public void setStrongAuth(boolean strongAuth) {
		this.strongAuth = strongAuth;
	}

	public String isStrongAuthUrl() {
		return strongAuthUrl;
	}

	public void setStrongAuthUrl(String strongAuthUrl) {
		this.strongAuthUrl = strongAuthUrl;
	}

	public String getCsrfToken() {
		return csrfToken;
	}

	public void setCsrfToken(String csrfToken) {
		this.csrfToken = csrfToken;
	}

	public String getStrongAuthUrl() {
		return strongAuthUrl;
	}
	
}
