package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class PersonalAccessTokenOwner {
	
	@SerializedName("type")
	String type;
	
	@SerializedName("id")
	String id;	

	@SerializedName("name")
	String name;

	public PersonalAccessTokenOwner() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
