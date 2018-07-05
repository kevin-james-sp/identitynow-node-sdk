package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiOrgData {

	@SerializedName("authErrorText")
	String authErrorText;

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

}

