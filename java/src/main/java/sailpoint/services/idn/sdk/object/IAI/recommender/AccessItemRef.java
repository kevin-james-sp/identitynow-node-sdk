package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

public class AccessItemRef {

	public AccessItemRef(String id, String type){
		this.id = id;
		//this.type = Type.valueOf(type);
		this.type = type;
	}

	@SerializedName("id")
	String id;

	@SerializedName("type")
	String type;

/*	enum Type{
		ENTITLEMENT,
		ROLE,
		ACCESS_PROFILE,
		ACCOUNT
	}*/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
