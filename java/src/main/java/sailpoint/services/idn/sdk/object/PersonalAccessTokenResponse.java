package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class PersonalAccessTokenResponse {
	
	@SerializedName("id")
	String id;
	
	@SerializedName("name")
	String name;
	
	@SerializedName("secret")
	String secret;
	
	@SerializedName("created")
	String created;
	
	@SerializedName("owner")
	PersonalAccessTokenOwner owner;

	public PersonalAccessTokenResponse() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public PersonalAccessTokenOwner getOwner() {
		return owner;
	}

	public void setOwner(PersonalAccessTokenOwner owner) {
		this.owner = owner;
	}
	
}
