package sailpoint.services.idn.sdk.object.accessrequest;

import com.google.gson.annotations.SerializedName;

public class AccessRevoke {

    @SerializedName("roleId")
    public String roleId;

    @SerializedName("identityId")
    public String identityId;
}
