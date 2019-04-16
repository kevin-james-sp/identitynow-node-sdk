package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

public class AccessItemRef {

	public AccessItemRef(String id, String type){
		this.id = id;
		this.type = Type.valueOf(type);
	}

	@SerializedName("id")
	String id;

	@SerializedName("type")
	Type type;

	enum Type{
		ENTITLEMENT,
		ROLE,
		ACCESS_PROFILE,
		ACCOUNT
	}
}
