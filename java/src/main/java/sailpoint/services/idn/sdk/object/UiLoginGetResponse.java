package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiLoginGetResponse {

	@SerializedName("ssoServerUrl")
	String ssoServerUrl;

	@SerializedName("goToOnFail")
	String goToOnFail;

	@SerializedName("loginUrl")
	String loginUrl;

	@SerializedName("auth")
	UiAuthData apiAuth;

	@SerializedName("org")
	UiOrgData apiOrg;

	public UiLoginGetResponse() {
	}

	public UiLoginGetResponse(String ssoServerUrl, String goToOnFail, UiAuthData apiAuth, UiOrgData apiOrg) {
		this.ssoServerUrl = ssoServerUrl;
		this.goToOnFail = goToOnFail;
		this.apiAuth = apiAuth;
		this.apiOrg = apiOrg;
	}

	public String getGoToOnFail() {
		return goToOnFail;
	}

	public void setGoToOnFailUrl(String goToOnFail) {
		this.goToOnFail = goToOnFail;
	}

	public UiAuthData getApiAuth() {
		return apiAuth;
	}

	public void setApiAuth(UiAuthData apiAuth) {
		this.apiAuth = apiAuth;
	}

	public UiOrgData getApiOrg() {
		return apiOrg;
	}

	public void setApiOrg(UiOrgData apiOrg) {
		this.apiOrg = apiOrg;
	}

	public String getSsoServerUrl() {
		return ssoServerUrl;
	}

	public void setSsoServerUrl(String ssoServerUrl) {
		this.ssoServerUrl = ssoServerUrl;
	}

	public String getLoginUrl() { return loginUrl; }

	public void setLoginUrl(String loginUrl) { this.loginUrl = loginUrl; }

}

