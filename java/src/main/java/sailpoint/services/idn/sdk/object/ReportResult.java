package sailpoint.services.idn.sdk.object;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ReportResult {
	
/*
{
  "date": 1492740160904,
  "duration": 2654,
  "id": "2c9180845b5e6325015b8e3f2d6f5fcb",
  "reportName": "Accounts Report-HR Source",
  "rows": 12,
  "status": "Success"
}
*/
	  @SerializedName("date")
	  public final Long date;
	
	  @SerializedName("duration")
	  public final int duration;
	  
	  @SerializedName("id")
	  public final String id;	  
	
	  @SerializedName("reportName")
	  public final String reportName;
	  
	  @SerializedName("rows")
	  public final int rows;
	  
	  @SerializedName("status")
	  public final String status;

	public ReportResult(Long date, int duration, String id, String reportName,
			int rows, String status) {
		super();
		this.date = date;
		this.duration = duration;
		this.id = id;
		this.reportName = reportName;
		this.rows = rows;
		this.status = status;
	}
	
	public boolean isComplete() {
		return "Success".equalsIgnoreCase( this.status );
	}
	
}
