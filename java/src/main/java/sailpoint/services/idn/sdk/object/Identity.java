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

}
