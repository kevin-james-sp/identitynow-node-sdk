package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class ApiLoginGetResponse {

	@SerializedName("ssoServerUrl")
	String ssoServerUrl;

	@SerializedName("goToOnFail")
	String goToOnFailUrl;

	@SerializedName("auth")
	ApiAuth apiAuth;

	@SerializedName("org")
	ApiOrg apiOrg;

	public ApiLoginGetResponse() {
	}

	public ApiLoginGetResponse(String ssoServerUrl, String goToOnFailUrl, ApiAuth apiAuth, ApiOrg apiOrg) {

		this.ssoServerUrl = ssoServerUrl;
		this.goToOnFailUrl = goToOnFailUrl;
		this.apiAuth = apiAuth;
		this.apiOrg = apiOrg;
	}

	public String getGoToOnFailUrl() {
		return goToOnFailUrl;
	}

	public void setGoToOnFailUrl(String goToOnFailUrl) {
		this.goToOnFailUrl = goToOnFailUrl;
	}

	public ApiAuth getApiAuth() {
		return apiAuth;
	}

	public void setApiAuth(ApiAuth apiAuth) {
		this.apiAuth = apiAuth;
	}

	public ApiOrg getApiOrg() {
		return apiOrg;
	}

	public void setApiOrg(ApiOrg apiOrg) {
		this.apiOrg = apiOrg;
	}

	public String getSsoServerUrl() {
		return ssoServerUrl;
	}

	public void setSsoServerUrl(String ssoServerUrl) {
		this.ssoServerUrl = ssoServerUrl;
	}

}

