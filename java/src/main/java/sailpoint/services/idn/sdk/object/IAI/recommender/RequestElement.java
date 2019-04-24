package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

public class RequestElement {

	public RequestElement(String identityId, AccessItemRef item){
		this.identityId = identityId;
		this.item = item;
	}

	@SerializedName("identityId")
	String identityId;

	@SerializedName("item")
	AccessItemRef item;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public AccessItemRef getItem() {
		return item;
	}

	public void setItem(AccessItemRef item) {
		this.item = item;
	}
}
