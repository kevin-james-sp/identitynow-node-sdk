package sailpoint.services.idn.sdk.object.IAI.Oauth;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

	@SerializedName("access_token")
	private String access_token;

	@SerializedName("token_type")
	private String tokenType;

	public String getTokenType(){
		return tokenType;
	}

	public String getAccess_token() {
		return access_token;
	}

	public String getAuthorization(){
		return getTokenType() + " " + getAccess_token();
	}
}
