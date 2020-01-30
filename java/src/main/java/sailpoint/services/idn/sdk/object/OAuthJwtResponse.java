package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class OAuthJwtResponse {
	
	@SerializedName("access_token")
	String accessToken;
	
	@SerializedName("token_type")
	String tokenType;
	
	@SerializedName("expires_in")
	int expiresIn;
	
	@SerializedName("strong_auth")
	boolean strongAuth;
	
	@SerializedName("scope")
	String scope;
	
	@SerializedName("tenant_id")
	String tenantId;
	
	@SerializedName("pod")
	String pod;
	
	@SerializedName("org")
	String org;
	
	@SerializedName("identity_id")
	String identityId;
	
	@SerializedName("user_name")
	String userName;
	
	@SerializedName("jti")
	String jti;

	public OAuthJwtResponse() {}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public boolean isStrongAuth() {
		return strongAuth;
	}

	public void setStrongAuth(boolean strongAuth) {
		this.strongAuth = strongAuth;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getPod() {
		return pod;
	}

	public void setPod(String pod) {
		this.pod = pod;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}
	
}

/*
{
	"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnRfaWQiOiI3YjRjMGZmNC02YjRkLTQ5OWMtYjU1OC05OGI5ZGVhYzg1MDkiLCJwb2QiOiJ0YWhpdGkiLCJvcmciOiJmYXN0ZmVkIiwiaWRlbnRpdHlfaWQiOiIyYzkxODA4YjZiMDliNGZkMDE2YjFmMzVkZDI2NDRkOCIsInVzZXJfbmFtZSI6InN1cHBvcnQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwic3Ryb25nX2F1dGgiOnRydWUsImV4cCI6MTU4MDQ0Nzc1MCwiYXV0aG9yaXRpZXMiOlsiT1JHX0FETUlOIl0sImp0aSI6ImJkYmZhZDUxLTUwZmMtNGU3NC1iMTU3LWYzMTEzNjA5MTkwMiIsImNsaWVudF9pZCI6ImFhZWU0MzNiOGY0NDRkNjZiYWI5MmY4N2U4OTlkYjk5In0.eZQt57baXtSbBgfm9ZTzLYB8edLJVyB9Uf-yrjLHtxk",
	"token_type":"bearer",
	"expires_in":43199,
	"scope":"read write",
	"strong_auth":true,
	"tenant_id":"7b4c0ff4-6b4d-499c-b558-98b9deac8509",
	"pod":"tahiti",
	"org":"fastfed",
	"identity_id":"2c91808b6b09b4fd016b1f35dd2644d8",
	"user_name":"support",
	"jti":"bdbfad51-50fc-4e74-b157-f31136091902"
}
*/
