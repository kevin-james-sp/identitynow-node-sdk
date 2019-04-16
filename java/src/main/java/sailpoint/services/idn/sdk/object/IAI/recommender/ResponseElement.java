package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

public class ResponseElement {

	@SerializedName("request")
	RequestElement request;

	@SerializedName("recommendation")
	Recommendation recommendation;

	@SerializedName("interpretations")
	String[] interpretations;

	public enum Recommendation{
		YES,
		NO,
		NOT_FOUND
	}

	public Recommendation getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Recommendation recommendation) {
		this.recommendation = recommendation;
	}

	public String[] getInterpretations() {
		return interpretations;
	}

	public void setInterpretations(String[] interpretations) {
		this.interpretations = interpretations;
	}

	public RequestElement getRequest() {

		return request;
	}

	public void setRequest(RequestElement request) {
		this.request = request;
	}
}
