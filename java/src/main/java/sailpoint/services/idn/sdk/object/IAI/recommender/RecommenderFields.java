package sailpoint.services.idn.sdk.object.IAI.recommender;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecommenderFields {

	@SerializedName("excludeInterpretations")
	boolean excludeInterpretations;

	@SerializedName("requests")
	ArrayList<RequestElement> requests;

	public boolean isExcludeInterpretations() {
		return excludeInterpretations;
	}

	public void setExcludeInterpretations(boolean excludeInterpretations) {
		this.excludeInterpretations = excludeInterpretations;
	}

	public ArrayList<RequestElement> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<RequestElement> requests) {
		this.requests = requests;
	}
}
