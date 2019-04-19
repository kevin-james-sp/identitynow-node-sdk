package sailpoint.concurrent.objects;

import sailpoint.services.idn.sdk.object.IAI.recommender.ResponseElement;

import java.util.List;

public class IDAMetrics {

	private boolean successful;
	private long responseTime;
	private List<ResponseElement> recommendations;
	private int responseCode;

	public IDAMetrics(boolean successful, long responseTime, List<ResponseElement> recommendations, int responseCode){
		this.successful = successful;
		this.responseTime = responseTime;
		this.recommendations = recommendations;
		this.responseCode = responseCode;

	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

	public List<ResponseElement> getRecommendations() { return recommendations; }

	public void setRecommendations(List<ResponseElement> recommendations) { this.recommendations = recommendations; }

	public int getResponseCode() { return responseCode; }

	public void setResponseCode(int responseCode) { this.responseCode = responseCode; }

}
