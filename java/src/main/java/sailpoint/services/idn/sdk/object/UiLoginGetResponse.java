package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiLoginGetResponse {

	@SerializedName("ssoServerUrl")
	String ssoServerUrl;

	@SerializedName("goToOnFail")
	String goToOnFailUrl;

	@SerializedName("auth")
	UiAuthData apiAuth;

	@SerializedName("org")
	UiOrgData apiOrg;

	public UiLoginGetResponse() {
	}

	public UiLoginGetResponse(String ssoServerUrl, String goToOnFailUrl, UiAuthData apiAuth, UiOrgData apiOrg) {

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

}

