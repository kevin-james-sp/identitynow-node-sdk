package sailpoint.services.idn.sdk.object;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Transform {
	
	@SerializedName("id")
	public String id;
	
	@SerializedName("type")
	public String type;
	
	@SerializedName("attributes")
	public Map<String,Object> attributes;

}
