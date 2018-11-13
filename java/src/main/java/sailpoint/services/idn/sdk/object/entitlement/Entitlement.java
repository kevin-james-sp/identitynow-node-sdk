package sailpoint.services.idn.sdk.object.entitlement;

import com.google.gson.annotations.SerializedName;

public class Entitlement {

    @SerializedName("id")
	public String id;

    @SerializedName("ownerId")
	public String ownerId;

    @SerializedName("description")
	public String description;

    @SerializedName("applicationName")
	public String applicationName;

    @SerializedName("value")
	public String value;

    @SerializedName("applicationId")
	public String applicationId;

    @SerializedName("attribute")
	public String attribute;

    @SerializedName("ownerUid")
	public String ownerUid;

    @SerializedName("displayableName")
	public String displayableName;

    @SerializedName("privileged")
	public String privileged;

    @SerializedName("displayName")
	public String displayName;
}
