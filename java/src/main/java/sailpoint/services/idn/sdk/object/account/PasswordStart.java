package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class PasswordStart {

    @SerializedName("user")
    public String user;

    @SerializedName("org")
    public String org;

    @SerializedName("process")
    public String process;

    public PasswordStart(String user, String org, String process) {
        this.user = user;
        this.org = org;
        this.process = process;
    }
}
