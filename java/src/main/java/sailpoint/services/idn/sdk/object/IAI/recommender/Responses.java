package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Responses {

	@SerializedName("responses")
	@Expose
	private List<ResponseElement> responses = null;

	public List<ResponseElement> getResponses() {
		return responses;
	}

	public void setResponses(List<ResponseElement> responses) {
		this.responses = responses;
	}
}
