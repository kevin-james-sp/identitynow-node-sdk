package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseElement {

	@SerializedName("request")
	@Expose
	private RequestElement requestElement;
	@SerializedName("recommendation")
	@Expose
	private String recommendation;
	@SerializedName("interpretations")
	@Expose
	private List<String> interpretations = null;

	public RequestElement getRequest() {
		return requestElement;
	}

	public void setRequest(RequestElement request) {
		this.requestElement = request;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public List<String> getInterpretations() {
		return interpretations;
	}

	public void setInterpretations(List<String> interpretations) {
		this.interpretations = interpretations;
	}

}
