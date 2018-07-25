package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * This class of data is returned from calls into /api/user/getStrongAuthnMethods
 * 
 * This returns a payload that models what Strong Authentication methods are 
 * available to the end user.  This call returns a List<> of these objects.
 *
 */
public class UiStrongAuthMethod {
	
	// Example JSON Array of a list of these objects:
	/*
	 [
	  {
	   "label":"BY_SMS_PERSONAL",
	   "description":"Send text to alternate phone",
	   "type":"CODE",
	   "strongAuthType":"SMS_PERSONAL"
	  },{
	   "label":"BY_VOICE_PERSONAL",
	   "description":"Send code by voice",
	   "type":"CODE",
	   "strongAuthType":"VOICE_PERSONAL"
	  },{
	   "label":"BY_KBA",
	   "description":"Security questions",
	   "type":"KBA",
	   "strongAuthType":"KBA"}
	  ]
	 */	
	@SerializedName("label")
	public String label;
	
	@SerializedName("description")
	public String description;
	
	@SerializedName("type")
	public String type;
	
	@SerializedName("strongAuthType")
	public String strongAuthType;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrongAuthType() {
		return strongAuthType;
	}

	public void setStrongAuthType(String strongAuthType) {
		this.strongAuthType = strongAuthType;
	}
	
}
