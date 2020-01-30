package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class PersonalAccessTokenRequest {
	
	@SerializedName("name")
	String name;

	public PersonalAccessTokenRequest() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
