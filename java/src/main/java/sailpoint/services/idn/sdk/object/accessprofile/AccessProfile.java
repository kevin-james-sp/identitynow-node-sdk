package sailpoint.services.idn.sdk.object.accessprofile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccessProfile {

	@SerializedName("approvalSchemes")
	public Object approvalSchemes;
	
	@SerializedName("deniedCommentsRequired")
	public Boolean deniedCommentsRequired;
	
	@SerializedName("description")
	public String description;
	
	@SerializedName("entitlements")
	public List<String> entitlements = null;
	
	@SerializedName("id")
	public String id;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("ownerId")
	public Integer ownerId;
	
	@SerializedName("protected")
	public Boolean _protected;
	
	@SerializedName("requestCommentsRequired")
	public Boolean requestCommentsRequired;
	
	@SerializedName("requestable")
	public Boolean requestable;
	
	@SerializedName("sourceId")
	public Integer sourceId;
	
	@SerializedName("sourceName")
	public String sourceName;
	
	@SerializedName("useForProvisioning")
	public Boolean useForProvisioning;
	
}
