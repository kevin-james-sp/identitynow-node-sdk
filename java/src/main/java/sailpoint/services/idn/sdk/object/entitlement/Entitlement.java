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

	public Entitlement(String id, String ownerId, String description, String applicationName, String value, String applicationId, String attribute, String ownerUid, String displayableName, String privileged, String displayName) {
		this.id = id;
		this.ownerId = ownerId;
		this.description = description;
		this.applicationName = applicationName;
		this.value = value;
		this.applicationId = applicationId;
		this.attribute = attribute;
		this.ownerUid = ownerUid;
		this.displayableName = displayableName;
		this.privileged = privileged;
		this.displayName = displayName;
	}

	public Entitlement(){ }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getOwnerUid() {
		return ownerUid;
	}

	public void setOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
	}

	public String getDisplayableName() {
		return displayableName;
	}

	public void setDisplayableName(String displayableName) {
		this.displayableName = displayableName;
	}

	public String getPrivileged() {
		return privileged;
	}

	public void setPrivileged(String privileged) {
		this.privileged = privileged;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
