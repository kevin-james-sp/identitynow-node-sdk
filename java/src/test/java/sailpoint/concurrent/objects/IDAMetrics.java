package sailpoint.concurrent.objects;

public class IDAMetrics {

	private boolean successful;
	private long responseTime;
	public String recommendation;

	public IDAMetrics(boolean successful, long responseTime, String recommendation){
		this.successful = successful;
		this.responseTime = responseTime;
		this.recommendation = recommendation;
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

	public String getRecommendation() { return recommendation; }

	public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

}
