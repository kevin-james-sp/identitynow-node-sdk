package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Identity {

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("displayName")
	@Expose
	private String displayName;

	@SerializedName("alias")
	@Expose
	private String alias;

	@SerializedName("email")
	@Expose
	private String email;

	@SerializedName("status")
	@Expose
	private String status;

	@SerializedName("enabled")
	@Expose
	private Boolean enabled;

	@SerializedName("pending")
	@Expose
	private Boolean pending;

	@SerializedName("externalId")
	@Expose
	private String externalId;

	@SerializedName("processingDetails")
	@Expose
	private Object processingDetails;

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Object getProcessingDetails() {
		return processingDetails;
	}

	public void setProcessingDetails(Object processingDetails) {
		this.processingDetails = processingDetails;
	}

}