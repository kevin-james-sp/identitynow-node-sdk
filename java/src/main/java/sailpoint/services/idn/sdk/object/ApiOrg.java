package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class ApiOrg {

	@SerializedName("authErrorText")
	String authErrorText;

	public ApiOrg() {
	}

	public ApiOrg(String authErrorText) {
		this.authErrorText = authErrorText;
	}

	public String getAuthErrorText() {
		return authErrorText;
	}

	public void setAuthErrorText(String authErrorText) {
		this.authErrorText = authErrorText;
	}

}

