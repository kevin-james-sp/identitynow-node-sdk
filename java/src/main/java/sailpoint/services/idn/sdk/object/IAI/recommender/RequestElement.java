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
}
