package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class PasswordPolicy {

    @SerializedName("errorText")
    public String errorText;

    @SerializedName("enabled")
    public boolean enabled;

    @SerializedName("encryptPassword")
    public boolean encryptPassword;

    @SerializedName("policy")
    public Policy policy;

    @SerializedName("org")
    public Org org;
}
