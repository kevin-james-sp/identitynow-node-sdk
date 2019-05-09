package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * A record associating an Identity with that Identity's manager in the 
 * corporate structure.
 * @author adam.hampton
 *
 */
public class IdentityManager {
	
	/**
	 * The Manager's human readable / display name.
	 */
	@SerializedName("displayName")
	public String displayName;
	
	/**
	 * The Manager's Identity's unique name identifier.  This is often an
	 * employee ID number, SAMAccountName or other string defining the 
	 * Manager. 
	 */
	@SerializedName("name")
	public String name;
	
	/**
	 * The 32-character GUID identifying the manager in the organization.
	 * Example: "id": "2c9180845f12071f015f1303d2d72c7e"
	 *  
	 */
	@SerializedName("id")
	public String id;

}
