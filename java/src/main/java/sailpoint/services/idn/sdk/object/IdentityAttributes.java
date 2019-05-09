package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * A map of attributes that can be associated with an Identity.
 * Some of these fields are stock "out of the box" fields while others
 * are Org specific and configured by the organization's administrator. 
 * 
 * This object is returned by the Elastic Search model of the Identity.
 * 
 * @author adam.hampton
 *
 */
public class IdentityAttributes {
	
	@SerializedName("uid")
	public String uid;
	
	@SerializedName("firstname")
	public String firstname;
	
	@SerializedName("lastname")
	public String lastname;
	
	@SerializedName("email")
	public String email;
	
	@SerializedName("personalEmail")
	public String personalEmail;
		
	@SerializedName("cloudStatus")
	public String cloudStatus;
	
	@SerializedName("displayName")
	public String displayName;
	
	/**
	 * The 32-character hexadecimal GUID string identifying the Source 
	 * that is the system of record defining this Identity.  Often this
	 * is an HR source or feed from an up-stream person management system
	 * but this can be simply from an Active Directory system as well
	 */
	@SerializedName("cloudAuthoritativeSource")	
	public String cloudAuthoritativeSource;
	

	/**
	 * An XML Model of the accounts lockout state peg counters.  
	 * Example format:
	 * <InvalidPassword>
	 *   <InvalidCount>0</InvalidCount>
	 *   <LastInvalidAt>0</LastInvalidAt>
	 *   <LockedoutAt>0</LockedoutAt>
	 *   <ActualLockoutDuration>900000</ActualLockoutDuration>
	 * </InvalidPassword> 
	 * TODO: Should we have a parser for this or is XML acceptable?
	 */
	@SerializedName("internal.lockoutState")
	public String lockoutState;
	
	@SerializedName("phone")
	public String phone;
	
	@SerializedName("internalCloudStatus")
	public String internalCloudStatus;
	
	@SerializedName("identificationNumber")
	public String identificationNumber;
	
	
	// TODO: Handle this list of IWA User Principal Name values:
	// "iplanet-am-user-alias-list": [],
	
	// Custom attributes from performance lab examples:
	// "workerType": "Employee",
    // "costCenter": "CC-AM",
    // "department": "Legal",

}
