package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * This class of data is returned from calls into /api/user/get 
 *    
 * This returns a model of a user of IdentityNow.  It includes many
 * attributes.  
 * 
 * @author adam.hampton
 *
 */
public class User {
	
	// Example JSON:
	/*
	{
	 "id":"11238566",
	 "alias":"support",
	 "uid":"support",
	 "name":"SailPoint Support",
	 "uuid":"9ba320c6-0b15-4b6c-a1aa-5cb60790f8a2",
	 "status":"UNREGISTERED",
	 "pending":false,
	 "encryptionKey":"GBIGNOjXs+CJHW9CuQg...",
	 "encryptionCheck":"8ea0f3a84d6238d48bd70fc0972de8::d733ccc4dbb1f887",
	 "passwordResetSinceLastLogin":false,
	 "usageCertAttested":null,
	 "userFlags":{},
	 "enabled":true,
	 "altAuthVia":"KBA",
	 "altAuthViaIntegrationData":null,
	 "kbaAnswers":1,
	 "disablePasswordReset":false,
	 "ptaSourceId":null,
	 "supportsPasswordPush":false,
	 "attributes":{"cloudStatus":"UNREGISTERED","displayName":"SailPoint Support","email":"cloud-support@sailpoint.com","firstname":"SailPoint","internal.lockoutHistory":null,"internal.lockoutState":"<InvalidPassword><InvalidCount>0<\u002fInvalidCount><LastInvalidAt>0<\u002fLastInvalidAt><LockedoutAt>0<\u002fLockedoutAt><ActualLockoutDuration>900000<\u002fActualLockoutDuration><\u002fInvalidPassword>","internalCloudStatus":"UNREGISTERED","iplanet-am-user-alias-list":null,"lastLoginTimestamp":1532487865677,"lastSyncDate":"b26b2d748d792d6c04100a8b16814c05ae9d99e33b5e2ccbcf51b56f5f4402c8","lastname":"Support","phone":"512-942-7578","uid":"support"},"externalId":"ff8081815e5f6067015e5f61d40a0322","role":["ORG_ADMIN"],"phone":"512-942-7578","email":"cloud-support@sailpoint.com","personalEmail":null,"employeeNumber":null,"riskScore":0,"featureFlags":{"MULTI_BRANDING_FROM_ADDRESS":true,"ATLAS_FF_HEALTH":true,"CERTIFICATION_PROVISION_USING_WORKFLOW":true,"ENABLE_RATS_SERVICE":true,"ENABLE_OTHER_REPORTS_RENAME":true,"ENABLE_ENTITLEMENT_OWNER":false,"STYX_FF_VALIDATION":false,"SEARCH_RECONCILIATION_FLAG":false,"E2E_FF_TEST":true,"SSO_USE_LOGIN_SERVICE":false,"CERT_ADMIN_UI":false,"property1":false,"jw_test":false,"CERTS_UI":false,"Test_1212":true,"PIPELINE_RESPONSE_SERVICE":true,"SEARCH_RECONCILE_PROVISIONING":false,"IWA_LOGOUT_FLOW":true,"IDENTITY_REFRESH_QUEUED_CHECKING":false,"ORPHAN_IDENTITIES_REPORT":true,"idntool":false,"va-live":false,"SEARCH_UI":true,"UI_DASHBOARD":true,"ENABLE_AUTO_REVOKE":true,"EXT_USER_UI":true,"ARMADA_NAVBAR_UPDATE":false,"ENABLE_RATS_MANTIS_JAR":false,"FEATURE_IDENTITY_REQUEST_PRUNE":true,"SEND_EVENTS_TO_SDS":true,"FEATURE_ADD_MISSING_INDEXES":true,"ROLE_REQUEST_UI":false,"PENDO_DESIGNER":false,"TWO_STAGE_MFA_PASSWD_RESET":false,"GOVERNANCE_GROUP_APPROVERS":true,"USE_ENTITLEMENT_INDEX":true,"SDS_IDENTITY_LIST_SEARCH":false,"STREAMING_AGGREGATION":false,"SEND_DIRECT_TO_AER":true,"SEARCH_CERT_UI":false,"ENABLE_AUTO_APPROVER_DELEGATION":true,"EXT_ADMIN_UI":true,"PENDO":true,"PENDO_STAGING":true,"PENDO_SUPPRESS":false,"ENTITLEMENT_SEARCH":true,"PASSWD_RESET_BY_CODE_EMAIL":true,"NAV_ADMIN_UI":false,"ROLE_REQUEST":false,"USE_CERT_DEFN_ID":false,"DASHBOARD_UI":false,"IDENTITY_ACCESS":true,"PROVISIONING_SEARCH":false,"DSCO_ORG_CLEANUP":false,"PASSWORD_NEVER_EXPIRES":false,"GOVERNANCE_GROUPS":true,"SEARCH_RECONCILE_DELETES_FLAG":true,"PENDO_FOR_ALL":false,"PENDO_FOR_ADMIN":true,"SEARCH_RECONCILE_IDENTITIES":true,"USE_V2_ENCRYPTION":true,"ENABLE_ENTITLEMENT_FILTER_AGGREGATION":true,"ENTITLEMENT_SOURCE_UPDATE_FLAG":false,"SSO_POST_LOGIN_SERVICE":false,"COMPLETING_CAMPAIGN_STATUS":false,"DISABLE_LIGHT_WEIGHT_DOHEALTH_CHECK":true,"SYNC_LEK_UNLOCK_VAULT":false,"TEST_FLAG_DEFAULT_VALUE":false,"PLUGIN_UI":false,"STREAM_CERTIFICATION_GENERATION":false,"LD_HEALTH_CHECK":true,"OAUTH_UI":true,"SEARCH_MULTIPLE_NOUNS":true,"royale":false,"MULTIPLE_NOUNS_UI":false,"ENABLE_LOGIN_SECURITY":true,"ANGULAR_RESET_UI":false,"SEARCH_AUTO_SUGGEST":true,"DISABLE_REMEDIATION_SCANS":false,"IDENTITY_SEARCH":false,"SEND_SMS_NOTIFICATION_FOR_PASSWORD_EXPIRATION":false,"EXT_REGISTRATION_UI":true,"PUBLISH_EVENTS_TO_IRIS":true,"IDNLOMBOK_173_RUN_JOB":true,"SEARCH_CONTENT_MANAGER_UI":false,"ENABLE_CAMPAIGN_COMPOSITION_REPORT":true,"NIGHTLY_SYNC_SEARCH":false,"SKIP_STUCK_CAMPAIGN_CHECK":false,"test_8":false,"REQUIRE_SOD_STATE":false,"test_7":false,"CMS_RUN_RULE_BEANSHELL":false,"SEARCH_SOD_UI":false},"feature":["PROVISIONING","PASSWORD_MANAGEMENT","REVERSE_PROXY","ADVANCED_AUTHENTICATION","SINGLE_SIGN_ON","CERTIFICATION"],"orgEncryptionKey":"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7DVPmDBfcVVbVPHLxUHK1PDxZRpBdKEclvzkYNyBAHLj5VLKEkyJtnndyWQut3EL1TgTuWQf4PniP7a1d4D/ZbipIkLOjopnaGT37a/nSFZuvUBcObqQCy5n05ZbrPb9lJsG06HOZ4qEqcu99d4ibkzyNqZPu2g1i8WM6ESgojWIi31y/5lAG2Af3TMKvQZa9neCQN2rCqmVjqqHOv7mKl3ETX7O6F/Mf/dDQBJwiT7MKFc/L3JXGqjFKkkHgxTmhwVgD7gMWcONSHN3wc2h/7jq6TjlOrQpYJt4HONxl8iw/TpOYMAxb9TWjrtNkPxcUtDy8kK6d67Ba2CHmMmqAQIDAQAB-----END PUBLIC KEY-----","orgEncryptionKeyId":"MGY4NDA5OTgtNzg4NC00ZGIyLTgzZWYtMGNkMzIxM2ZiNjAz","meta":{},"org":{"name":"perflab-09072140","scriptName":"perflab-09072140","mode":"IDAAS","numQuestions":8,"status":"test","maxRegisteredUsers":320000,"pod":"dev01-useast1","pwdResetPersonalPhone":false,"pwdResetPersonalEmail":false,"pwdResetKba":false,"pwdResetEmail":false,"pwdResetDuo":false,"authErrorText":"","strongAuthKba":true,"strongAuthPersonalPhone":false,"strongAuthPersonalEmail":false,"integrations":[],"productName":"SailPoint","kbaReqForAuthn":1,"kbaReqAnswers":1,"lockoutAttemptThreshold":5,"lockoutTimeMinutes":15,"usageCertRequired":false,"adminStrongAuthRequired":true,"enableExternalPasswordChange":false,"enablePasswordReplay":true,"enableAutomaticPasswordReplay":true,"notifyAuthenticationSettingChange":true,"netmasks":null,"countryCodes":null,"whiteList":false,"usernameEmptyText":null,"usernameLabel":null,"enableAutomationGeneration":false,"emailTestMode":false,"emailTestAddress":null,"passwordReplayState":"enabled","systemNotificationConfig":"{\"notifications\":[{\"type\":\"Applications\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Identities\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Sources\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Virtual Appliances\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}}],\"recipientType\":\"specificUsers\"}","maxClusterDebugHours":"24","brandName":"default","logo":null,"emailFromAddress":null,"standardLogoUrl":null,"narrowLogoUrl":null,"actionButtonColor":"20B2DE","activeLinkColor":"20B2DE","navigationColor":"011E69"},
	 "stepUpAuth":false,
	 "bxInstallPrompted":false,
	 "federatedLogin":false,
	 "auth":{"encryption":"hash","service":"ldapService"},
	 "onNetwork":false,
	 "onTrustedGeo":true}
	 .. "externalId":"ff8081815e5f6067015e5f61d40a0322","role":["ORG_ADMIN"],"phone":"512-942-7578","email":"cloud-support@sailpoint.com","personalEmail":null,"employeeNumber":null,"riskScore":0,"featureFlags":{"MULTI_BRANDING_FROM_ADDRESS":true,"ATLAS_FF_HEALTH":true,"CERTIFICATION_PROVISION_USING_WORKFLOW":true,"ENABLE_RATS_SERVICE":true,"ENABLE_OTHER_REPORTS_RENAME":true,"ENABLE_ENTITLEMENT_OWNER":false,"STYX_FF_VALIDATION":false,"SEARCH_RECONCILIATION_FLAG":false,"E2E_FF_TEST":true,"SSO_USE_LOGIN_SERVICE":false,"CERT_ADMIN_UI":false,"property1":false,"jw_test":false,"CERTS_UI":false,"Test_1212":true,"PIPELINE_RESPONSE_SERVICE":true,"SEARCH_RECONCILE_PROVISIONING":false,"IWA_LOGOUT_FLOW":true,"IDENTITY_REFRESH_QUEUED_CHECKING":false,"ORPHAN_IDENTITIES_REPORT":true,"idntool":false,"va-live":false,"SEARCH_UI":true,"UI_DASHBOARD":true,"ENABLE_AUTO_REVOKE":true,"EXT_USER_UI":true,"ARMADA_NAVBAR_UPDATE":false,"ENABLE_RATS_MANTIS_JAR":false,"FEATURE_IDENTITY_REQUEST_PRUNE":true,"SEND_EVENTS_TO_SDS":true,"FEATURE_ADD_MISSING_INDEXES":true,"ROLE_REQUEST_UI":false,"PENDO_DESIGNER":false,"TWO_STAGE_MFA_PASSWD_RESET":false,"GOVERNANCE_GROUP_APPROVERS":true,"USE_ENTITLEMENT_INDEX":true,"SDS_IDENTITY_LIST_SEARCH":false,"STREAMING_AGGREGATION":false,"SEND_DIRECT_TO_AER":true,"SEARCH_CERT_UI":false,"ENABLE_AUTO_APPROVER_DELEGATION":true,"EXT_ADMIN_UI":true,"PENDO":true,"PENDO_STAGING":true,"PENDO_SUPPRESS":false,"ENTITLEMENT_SEARCH":true,"PASSWD_RESET_BY_CODE_EMAIL":true,"NAV_ADMIN_UI":false,"ROLE_REQUEST":false,"USE_CERT_DEFN_ID":false,"DASHBOARD_UI":false,"IDENTITY_ACCESS":true,"PROVISIONING_SEARCH":false,"DSCO_ORG_CLEANUP":false,"PASSWORD_NEVER_EXPIRES":false,"GOVERNANCE_GROUPS":true,"SEARCH_RECONCILE_DELETES_FLAG":true,"PENDO_FOR_ALL":false,"PENDO_FOR_ADMIN":true,"SEARCH_RECONCILE_IDENTITIES":true,"USE_V2_ENCRYPTION":true,"ENABLE_ENTITLEMENT_FILTER_AGGREGATION":true,"ENTITLEMENT_SOURCE_UPDATE_FLAG":false,"SSO_POST_LOGIN_SERVICE":false,"COMPLETING_CAMPAIGN_STATUS":false,"DISABLE_LIGHT_WEIGHT_DOHEALTH_CHECK":true,"SYNC_LEK_UNLOCK_VAULT":false,"TEST_FLAG_DEFAULT_VALUE":false,"PLUGIN_UI":false,"STREAM_CERTIFICATION_GENERATION":false,"LD_HEALTH_CHECK":true,"OAUTH_UI":true,"SEARCH_MULTIPLE_NOUNS":true,"royale":false,"MULTIPLE_NOUNS_UI":false,"ENABLE_LOGIN_SECURITY":true,"ANGULAR_RESET_UI":false,"SEARCH_AUTO_SUGGEST":true,"DISABLE_REMEDIATION_SCANS":false,"IDENTITY_SEARCH":false,"SEND_SMS_NOTIFICATION_FOR_PASSWORD_EXPIRATION":false,"EXT_REGISTRATION_UI":true,"PUBLISH_EVENTS_TO_IRIS":true,"IDNLOMBOK_173_RUN_JOB":true,"SEARCH_CONTENT_MANAGER_UI":false,"ENABLE_CAMPAIGN_COMPOSITION_REPORT":true,"NIGHTLY_SYNC_SEARCH":false,"SKIP_STUCK_CAMPAIGN_CHECK":false,"test_8":false,"REQUIRE_SOD_STATE":false,"test_7":false,"CMS_RUN_RULE_BEANSHELL":false,"SEARCH_SOD_UI":false},"feature":["PROVISIONING","PASSWORD_MANAGEMENT","REVERSE_PROXY","ADVANCED_AUTHENTICATION","SINGLE_SIGN_ON","CERTIFICATION"],"orgEncryptionKey":"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7DVPmDBfcVVbVPHLxUHK1PDxZRpBdKEclvzkYNyBAHLj5VLKEkyJtnndyWQut3EL1TgTuWQf4PniP7a1d4D/ZbipIkLOjopnaGT37a/nSFZuvUBcObqQCy5n05ZbrPb9lJsG06HOZ4qEqcu99d4ibkzyNqZPu2g1i8WM6ESgojWIi31y/5lAG2Af3TMKvQZa9neCQN2rCqmVjqqHOv7mKl3ETX7O6F/Mf/dDQBJwiT7MKFc/L3JXGqjFKkkHgxTmhwVgD7gMWcONSHN3wc2h/7jq6TjlOrQpYJt4HONxl8iw/TpOYMAxb9TWjrtNkPxcUtDy8kK6d67Ba2CHmMmqAQIDAQAB-----END PUBLIC KEY-----","orgEncryptionKeyId":"MGY4NDA5OTgtNzg4NC00ZGIyLTgzZWYtMGNkMzIxM2ZiNjAz","meta":{},"org":{"name":"perflab-09072140","scriptName":"perflab-09072140","mode":"IDAAS","numQuestions":8,"status":"test","maxRegisteredUsers":320000,"pod":"dev01-useast1","pwdResetPersonalPhone":false,"pwdResetPersonalEmail":false,"pwdResetKba":false,"pwdResetEmail":false,"pwdResetDuo":false,"authErrorText":"","strongAuthKba":true,"strongAuthPersonalPhone":false,"strongAuthPersonalEmail":false,"integrations":[],"productName":"SailPoint","kbaReqForAuthn":1,"kbaReqAnswers":1,"lockoutAttemptThreshold":5,"lockoutTimeMinutes":15,"usageCertRequired":false,"adminStrongAuthRequired":true,"enableExternalPasswordChange":false,"enablePasswordReplay":true,"enableAutomaticPasswordReplay":true,"notifyAuthenticationSettingChange":true,"netmasks":null,"countryCodes":null,"whiteList":false,"usernameEmptyText":null,"usernameLabel":null,"enableAutomationGeneration":false,"emailTestMode":false,"emailTestAddress":null,"passwordReplayState":"enabled","systemNotificationConfig":"{\"notifications\":[{\"type\":\"Applications\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Identities\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Sources\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}},{\"type\":\"Virtual Appliances\",\"byEmail\":false,\"thresholds\":{\"healthy\":\"\",\"unhealthy\":\"\"}}],\"recipientType\":\"specificUsers\"}","maxClusterDebugHours":"24","brandName":"default","logo":null,"emailFromAddress":null,"standardLogoUrl":null,"narrowLogoUrl":null,"actionButtonColor":"20B2DE","activeLinkColor":"20B2DE","navigationColor":"011E69"
	 */
	
	@SerializedName("id")
	public String id;
	
	@SerializedName("alias")
	public String alias;
	
	@SerializedName("uid")
	public String uid;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("kbaReqForAuthn")
	public int kbaReqForAuthn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKbaReqForAuthn() {
		return kbaReqForAuthn;
	}

	public void setKbaReqForAuthn(int kbaReqForAuthn) {
		this.kbaReqForAuthn = kbaReqForAuthn;
	}
	
}
