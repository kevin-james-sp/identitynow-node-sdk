package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class ApiSailpointApiData {
	
	// Exmample JSON:
	/*
	 {
	  "authType":"OAuth2.0",
	  "baseUrl":"https://perflab-09072140.api.cloud.sailpoint.com",
	  "logoutUrl":"https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/logout"
	  }
	 */
	
	@SerializedName("authType")
	public String authType;
	
	@SerializedName("baseUrl")
	public String baseUrl;
	
	@SerializedName("logoutUrl")
	public String logoutUrl;

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
	

}
