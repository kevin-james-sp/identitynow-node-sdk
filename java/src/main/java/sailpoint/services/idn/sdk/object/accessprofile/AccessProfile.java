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
	public String ownerId;
	
	@SerializedName("protected")
	public Boolean _protected;
	
	@SerializedName("requestCommentsRequired")
	public Boolean requestCommentsRequired;
	
	@SerializedName("requestable")
	public Boolean requestable;
	
	@SerializedName("sourceId")
	public String sourceId;
	
	@SerializedName("sourceName")
	public String sourceName;
	
	@SerializedName("useForProvisioning")
	public Boolean useForProvisioning;

	public Object getApprovalSchemes() {
		return approvalSchemes;
	}

	public void setApprovalSchemes(Object approvalSchemes) {
		this.approvalSchemes = approvalSchemes;
	}

	public Boolean getDeniedCommentsRequired() {
		return deniedCommentsRequired;
	}

	public void setDeniedCommentsRequired(Boolean deniedCommentsRequired) {
		this.deniedCommentsRequired = deniedCommentsRequired;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getEntitlements() {
		return entitlements;
	}

	public void setEntitlements(List<String> entitlements) {
		this.entitlements = entitlements;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean get_protected() {
		return _protected;
	}

	public void set_protected(Boolean _protected) {
		this._protected = _protected;
	}

	public Boolean getRequestCommentsRequired() {
		return requestCommentsRequired;
	}

	public void setRequestCommentsRequired(Boolean requestCommentsRequired) {
		this.requestCommentsRequired = requestCommentsRequired;
	}

	public Boolean getRequestable() {
		return requestable;
	}

	public void setRequestable(Boolean requestable) {
		this.requestable = requestable;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Boolean getUseForProvisioning() {
		return useForProvisioning;
	}

	public void setUseForProvisioning(Boolean useForProvisioning) {
		this.useForProvisioning = useForProvisioning;
	}
}
