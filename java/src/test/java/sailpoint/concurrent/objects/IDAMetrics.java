package sailpoint.concurrent.objects;

public class IDAMetrics {

	private boolean successful;
	private long responseTime;

	public IDAMetrics(boolean successful, long responseTime){
		this.successful = successful;
		this.responseTime = responseTime;
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

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

}
