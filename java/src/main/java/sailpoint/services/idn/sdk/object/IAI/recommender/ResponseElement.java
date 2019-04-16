package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

public class ResponseElement {

	@SerializedName("request")
	RequestElement request;

	@SerializedName("recommendation")
	Recommendation recommendation;

	@SerializedName("interpretations")
	String[] interpretations;

	enum Recommendation{
		YES,
		NO,
		NOT_FOUND
	}
}
