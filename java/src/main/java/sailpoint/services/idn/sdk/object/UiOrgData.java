package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiOrgData {
	
	@SerializedName("name")
	String name;
	
	@SerializedName("scriptName")
	String scriptName;
	
	@SerializedName("mode")
	String mode;
	
	@SerializedName("numQuestions")
	int numQuestions;
	
	@SerializedName("status")
	String status;
	
	@SerializedName("maxRegisteredUsers")
	int maxRegisteredUsers;
	
	@SerializedName("pod")
	String pod;
	
	// TODO: Eventually support these booleans.
	// "pwdResetPersonalPhone":false,
	// "pwdResetPersonalEmail":false,
	// "pwdResetKba":false,
	// "pwdResetEmail":false,
	// "pwdResetDuo":false,
	// "pwdResetPhoneMask":false,
	
	@SerializedName("authErrorText")
	String authErrorText;
	
	@SerializedName("strongAuthKba")
	boolean strongAuthKba;
	
	@SerializedName("strongAuthPersonalPhone")
	boolean strongAuthPersonalPhone;
	
	@SerializedName("strongAuthPersonalEmail")
	boolean strongAuthPersonalEmail;

	// TODO: 
	// "integrations":[],
	
	@SerializedName("productName")
	String productName;
		
	@SerializedName("kbaReqForAuthn")
	int kbaReqForAuthn;
	
	@SerializedName("kbaReqAnswers")
	int kbaReqAnswers;
	
	@SerializedName("lockoutAttemptThreshold")
	int lockoutAttemptThreshold;

	@SerializedName("lockoutTimeMinutes")
	int lockoutTimeMinutes;

	@SerializedName("usageCertRequired")
	boolean usageCertRequired;
	
	@SerializedName("adminStrongAuthRequired")
	boolean adminStrongAuthRequired;
	
	@SerializedName("enableExternalPasswordChange")
	boolean enableExternalPasswordChange;
	
	@SerializedName("enablePasswordReplay")
	boolean enablePasswordReplay;
	
	@SerializedName("enableAutomaticPasswordReplay")
	boolean enableAutomaticPasswordReplay;
	
	@SerializedName("notifyAuthenticationSettingChange")
	boolean notifyAuthenticationSettingChange;
	
	// TODO: 
	// "netmasks":null,
	// "countryCodes":null,
	// "whiteList":false,
	// "usernameEmptyText":null,
	// "usernameLabel":null,
	// "enableAutomationGeneration":false,
	// "emailTestMode":false,
	// "emailTestAddress":null,
	
	@SerializedName("orgType")
	String orgType;
	
	@SerializedName("passwordReplayState")
	String passwordReplayState;
	
	// TODO:
	// "systemNotificationConfig":"{\"notifications\":[{\"type\":\"Applications\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Identities\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Sources\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Virtual Appliances\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}}],\"recipientType\":\"specificUsers\"}",
	// "maxClusterDebugHours":"24",
	// "brandName":"default",
	// "logo":"https://sptcbu-images-useast1.s3.amazonaws.com/custom-logos/fastfed/3629ff49f2bd9fc39a4ebe3baf2a9dbd.png",
	// "emailFromAddress":null,
	// "standardLogoUrl":"https://sptcbu-images-useast1.s3.amazonaws.com/custom-logos/fastfed/3629ff49f2bd9fc39a4ebe3baf2a9dbd.png","narrowLogoUrl":"https://sptcbu-images-useast1.s3.amazonaws.com/custom-logos/fastfed/3629ff49f2bd9fc39a4ebe3baf2a9dbd.png","actionButtonColor":"20B2DE","activeLinkColor":"20B2DE","navigationColor":"F7931E"},

	public UiOrgData() {
	}

	public UiOrgData(String authErrorText) {
		this.authErrorText = authErrorText;
	}

	public String getAuthErrorText() {
		return authErrorText;
	}

	public void setAuthErrorText(String authErrorText) {
		this.authErrorText = authErrorText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMaxRegisteredUsers() {
		return maxRegisteredUsers;
	}

	public void setMaxRegisteredUsers(int maxRegisteredUsers) {
		this.maxRegisteredUsers = maxRegisteredUsers;
	}

	public String getPod() {
		return pod;
	}

	public void setPod(String pod) {
		this.pod = pod;
	}

	public boolean isStrongAuthKba() {
		return strongAuthKba;
	}

	public void setStrongAuthKba(boolean strongAuthKba) {
		this.strongAuthKba = strongAuthKba;
	}

	public boolean isStrongAuthPersonalPhone() {
		return strongAuthPersonalPhone;
	}

	public void setStrongAuthPersonalPhone(boolean strongAuthPersonalPhone) {
		this.strongAuthPersonalPhone = strongAuthPersonalPhone;
	}

	public boolean isStrongAuthPersonalEmail() {
		return strongAuthPersonalEmail;
	}

	public void setStrongAuthPersonalEmail(boolean strongAuthPersonalEmail) {
		this.strongAuthPersonalEmail = strongAuthPersonalEmail;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getKbaReqForAuthn() {
		return kbaReqForAuthn;
	}

	public void setKbaReqForAuthn(int kbaReqForAuthn) {
		this.kbaReqForAuthn = kbaReqForAuthn;
	}

	public int getKbaReqAnswers() {
		return kbaReqAnswers;
	}

	public void setKbaReqAnswers(int kbaReqAnswers) {
		this.kbaReqAnswers = kbaReqAnswers;
	}

	public int getLockoutAttemptThreshold() {
		return lockoutAttemptThreshold;
	}

	public void setLockoutAttemptThreshold(int lockoutAttemptThreshold) {
		this.lockoutAttemptThreshold = lockoutAttemptThreshold;
	}

	public int getLockoutTimeMinutes() {
		return lockoutTimeMinutes;
	}

	public void setLockoutTimeMinutes(int lockoutTimeMinutes) {
		this.lockoutTimeMinutes = lockoutTimeMinutes;
	}

	public boolean isUsageCertRequired() {
		return usageCertRequired;
	}

	public void setUsageCertRequired(boolean usageCertRequired) {
		this.usageCertRequired = usageCertRequired;
	}

	public boolean isAdminStrongAuthRequired() {
		return adminStrongAuthRequired;
	}

	public void setAdminStrongAuthRequired(boolean adminStrongAuthRequired) {
		this.adminStrongAuthRequired = adminStrongAuthRequired;
	}

	public boolean isEnableExternalPasswordChange() {
		return enableExternalPasswordChange;
	}

	public void setEnableExternalPasswordChange(boolean enableExternalPasswordChange) {
		this.enableExternalPasswordChange = enableExternalPasswordChange;
	}

	public boolean isEnablePasswordReplay() {
		return enablePasswordReplay;
	}

	public void setEnablePasswordReplay(boolean enablePasswordReplay) {
		this.enablePasswordReplay = enablePasswordReplay;
	}

	public boolean isEnableAutomaticPasswordReplay() {
		return enableAutomaticPasswordReplay;
	}

	public void setEnableAutomaticPasswordReplay(boolean enableAutomaticPasswordReplay) {
		this.enableAutomaticPasswordReplay = enableAutomaticPasswordReplay;
	}

	public boolean isNotifyAuthenticationSettingChange() {
		return notifyAuthenticationSettingChange;
	}

	public void setNotifyAuthenticationSettingChange(boolean notifyAuthenticationSettingChange) {
		this.notifyAuthenticationSettingChange = notifyAuthenticationSettingChange;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getPasswordReplayState() {
		return passwordReplayState;
	}

	public void setPasswordReplayState(String passwordReplayState) {
		this.passwordReplayState = passwordReplayState;
	}

}

