package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class PasswordPoll {

    @SerializedName("state")
    public String state;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("targetName")
    public String targetName;

    @SerializedName("JPT")
    public String JPT;
}
