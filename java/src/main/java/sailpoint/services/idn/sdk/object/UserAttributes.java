package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * This class of data inside what is returned from calls into /api/user/get 
 * This is really a sub-object of the User object in a key called 'attributes'.
 * 
 * @author adam.hampton
 *
 */
public class UserAttributes {
	
	// Example JSON:
	/*
	{
	 "cloudStatus":"UNREGISTERED",
	 "displayName":"SailPoint Support",
	 "email":"cloud-support@sailpoint.com",
	 "firstname":"SailPoint",
	 "internal.lockoutHistory":null,
	 "internal.lockoutState":"<InvalidPassword><InvalidCount>0<\u002fInvalidCount><LastInvalidAt>0<\u002fLastInvalidAt><LockedoutAt>0<\u002fLockedoutAt><ActualLockoutDuration>900000<\u002fActualLockoutDuration><\u002fInvalidPassword>",
	 "internalCloudStatus":"UNREGISTERED",
	 "iplanet-am-user-alias-list":null,
	 "lastLoginTimestamp":1532487865677,
	 "lastSyncDate":"b26b2d748d792d6c04100a8b16814c05ae9d99e33b5e2ccbcf51b56f5f4402c8",
	 "lastname":"Support",
	 "phone":"512-942-7578",
	 "uid":"support"
	},
	 }
	 */
	
	@SerializedName("uid")
	public String uid;
	
	@SerializedName("email")
	public String email;
	
	@SerializedName("cloudStatus")
	public String cloudStatus;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCloudStatus() {
		return cloudStatus;
	}

	public void setCloudStatus(String cloudStatus) {
		this.cloudStatus = cloudStatus;
	}
		
	// TODO: Add more fields to complete the model.
	
}
