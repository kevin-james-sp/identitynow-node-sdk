package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiSailpointGlobals {
	
	// Example JSON:
	/*
	 * {  
	  "analytics": {"google":{"trackingId":"UA-47107321-4"},"pendo":{"apiKey":"e8378f45-d012-4f3c-64b8-087c85be75d8","subscriptionId":"5683840649003008"}},
		"angularLocale": "en-us",
		"baseUrl": "https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/login/login/",
		"languagePackage": "en",
		"locales": ["en"],
		"uiModuleCache": "STATIC",
		"uiModuleName": "default",
		"uiModuleType": "AUTH",
		"uiTemplateBaseUrl": "https://d228lhlqtajrbe.cloudfront.net/modules/builds/auth/build281/",
		"uiTemplateDomainUrl": "https://d228lhlqtajrbe.cloudfront.net/",
		"uiTemplateUrl": "https://d228lhlqtajrbe.cloudfront.net/modules/builds/auth/build281/index.html",
		"api": {"authType":"OAuth2.0","baseUrl":"https://perflab-09072140.api.cloud.sailpoint.com","logoutUrl":"https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/logout"},  "analytics": {"google":{"trackingId":"UA-47107321-4"},"pendo":{"apiKey":"e8378f45-d012-4f3c-64b8-087c85be75d8","subscriptionId":"5683840649003008"}},
		"error": "false",
		"goto": "https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/ui",
		"gotoOnFail": "https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/login/fail/default/",
		"hasNetworkConstraints": "false",
		"loginUrl": "https://dev01-useast1-sso.cloud.sailpoint.com/sso/login",
		"orgNarrowLogoUrl": "",
		"orgProductName": "SailPoint",
		"orgScriptName": "perflab-09072140",
		"orgStandardLogoUrl": "",
		"rememberMe": true,
		"resetUrl": "https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/passwordreset/default/",
		"sso": true,
		"usernameEmptyText": "",
		"usernameLabel": "",
		"loginInformationalMessage": ""
	}
	 */
	
	@SerializedName("api")
	public UiApiGatewayData api;
	
	@SerializedName("baseUrl")
	public String baseUrl;
	
	@SerializedName("loginUrl")
	public String loginUrl;
	
	@SerializedName("orgScriptName")
	public String orgScriptName;

	@SerializedName("resetUrl")
	public String resetUrl;
	
	@SerializedName("sso")
	public boolean sso;
	
	@SerializedName("gotoOnFail")
	public String gotoOnFail;

	public UiApiGatewayData getApi() {
		return api;
	}

	public void setApi(UiApiGatewayData api) {
		this.api = api;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getOrgScriptName() {
		return orgScriptName;
	}

	public void setOrgScriptName(String orgScriptName) {
		this.orgScriptName = orgScriptName;
	}

	public String getResetUrl() {
		return resetUrl;
	}

	public void setResetUrl(String resetUrl) {
		this.resetUrl = resetUrl;
	}

	public boolean isSso() {
		return sso;
	}

	public void setSso(boolean sso) {
		this.sso = sso;
	}

	public String getGotoOnFail() {
		return gotoOnFail;
	}

	public void setGotoOnFail(String gotoOnFail) {
		this.gotoOnFail = gotoOnFail;
	}
	
}
