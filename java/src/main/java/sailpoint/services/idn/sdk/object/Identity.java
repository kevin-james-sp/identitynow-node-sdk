package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;
import sailpoint.services.idn.sdk.object.source.Source;

/**
 * An Identity object.  An Identity represents a physical person or a logical 
 * person (like a service account) that has accounts correlated to it.  An
 * Identity may own things, like Sources or Certifications, or Entitlements.
 * An Identity has other objects related to it, like Entitlements from accounts. 
 * 
 * @author adam.hampton
 *
 */
public class Identity {


    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("modified")
    public String modified;
	
	@SerializedName("attributes")
	public IdentityAttributes attributes;

    @SerializedName("accessCount")
    public Integer accessCount;
	
	/**
	 * The number of Accounts from aggregated Sources correlated to this Identity.
	 */
    @SerializedName("accountCount")
    public Integer accountCount;

	@SerializedName("entitlementCount")
    public Integer entitlementCount;
	
	@SerializedName("manager")
	public Identity manager;
	
	/**
	 * The ISO formatted date for when this Identity record was created in the Org.
	 */
    @SerializedName("created")
    public String created;

    @SerializedName("displayName")
    public String displayName;

    @SerializedName("source")
    public Source source;

    @SerializedName("employeeNumber")
    public String employeeNumber;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("inactive")
    public Boolean inactive;

    @SerializedName("phone")
    public String phone;

    @SerializedName("accessProfileCount")
    public Integer accessProfileCount;

    @SerializedName("email")
    public String email;

    @SerializedName("isManager")
    public Boolean isManager;

    @SerializedName("roleCount")
    public Integer roleCount;

    @SerializedName("status")
    public String status;

    @SerializedName("synced")
    public String synced;

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

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public IdentityAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(IdentityAttributes attributes) {
		this.attributes = attributes;
	}

	public Integer getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(Integer accessCount) {
		this.accessCount = accessCount;
	}

	public Integer getAccountCount() {
		return accountCount;
	}

	public void setAccountCount(Integer accountCount) {
		this.accountCount = accountCount;
	}

	public Integer getEntitlementCount() {
		return entitlementCount;
	}

	public void setEntitlementCount(Integer entitlementCount) {
		this.entitlementCount = entitlementCount;
	}

	public Identity getManager() {
		return manager;
	}

	public void setManager(Boolean manager) {
		isManager = manager;
	}

	public Integer getRoleCount() {
		return roleCount;
	}

	public void setRoleCount(Integer roleCount) {
		this.roleCount = roleCount;
	}
}
