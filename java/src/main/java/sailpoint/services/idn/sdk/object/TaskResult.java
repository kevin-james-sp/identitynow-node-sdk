package sailpoint.services.idn.sdk.object;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class TaskResult {
	
	  //{"attributes":{},"completed":null,"completionStatus":null,"created":1492729172613,"description":null,"id":"2c9180845b5e6325015b8d9792855aad","launched":null,"launcher":"slpt.services","messages":[],"name":"Accounts Report-HR Source","parentName":"Accounts Report","progress":null,"returns":[],"type":"QUARTZ"}
	  
	  @SerializedName("attributes")
	  public final Map<String,Object> attributes;
	  
	  @SerializedName("completed")
	  public final String completed;
	  
	  @SerializedName("completionStatus")
	  public final String completionStatus;
	  
	  @SerializedName("created")
	  public final Long created;
	  
	  @SerializedName("description")
	  public final String description;
	  
	  @SerializedName("id")
	  public final String id;
	  
	  @SerializedName("launched")
	  public final String launched;
	  
	  @SerializedName("launcher")
	  public final String launcher;
	  
	  @SerializedName("messages")
	  public final List<String> messages;
	  
	  @SerializedName("name")
	  public final String name;
	  
	  @SerializedName("parentName")
	  public final String parentName;
	  
	  @SerializedName("progress")
	  public final String progress;
	  
	  @SerializedName("returns")
	  public final List<String> returns;
	  
	  @SerializedName("type")
	  public final String type;

	public TaskResult(Map<String, Object> attributes, String completed,
			String completionStatus, Long created, String description,
			String id, String launched, String launcher, List<String> messages,
			String name, String parentName, String progress,
			List<String> returns, String type) {
		super();
		this.attributes = attributes;
		this.completed = completed;
		this.completionStatus = completionStatus;
		this.created = created;
		this.description = description;
		this.id = id;
		this.launched = launched;
		this.launcher = launcher;
		this.messages = messages;
		this.name = name;
		this.parentName = parentName;
		this.progress = progress;
		this.returns = returns;
		this.type = type;
	}
}
