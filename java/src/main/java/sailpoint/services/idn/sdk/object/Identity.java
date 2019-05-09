package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

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
	
	@SerializedName("lastModified")
	public String lastModified;
	
	@SerializedName("attributes")
	public IdentityAttributes attributes;
	
	@SerializedName("accessCount")
	public int accessCount;
	
	/**
	 * The number of Accounts from aggregated Sources correlated to this Identity.
	 */
	@SerializedName("accountCount")
	public int accountCount;
	
	@SerializedName("entitlementCount")
	public int entitlementCount;
	
	@SerializedName("manager")
	public IdentityManager manager;
	
	/**
	 * The ISO formatted date for when this Identity record was created in the Org.
	 */
	@SerializedName("created")
	public String created;
	
	@SerializedName("displayName")
	public String displayName;

	// TODO: Handle 'source'
//	  "source": {
//	    "name": "HR Source",
//	    "id": "2c9180855f12045e015f124c61f903ed"
//	  },

	@SerializedName("employeeNumber")
	public String employeeNumber;

	
	@SerializedName("firstName")
	public String firstName;
	
	@SerializedName("processingState")
	public String processingState;
	
	@SerializedName("inactive")
	public boolean inactive;
	
	@SerializedName("phone")
	public boolean phone;
	
	@SerializedName("processingDetails")
	public String processingDetails;

}
