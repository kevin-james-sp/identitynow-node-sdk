package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class PasswordReset {

    @SerializedName("username")
    public String username;

    @SerializedName("passwd")
    public String passwd;

    @SerializedName("usernameChange")
    public String usernameChange;

    @SerializedName("passwdChange")
    public String passwdChange;

    @SerializedName("publicKeyId")
    public String publicKeyId;

    public PasswordReset (String username, String passwd, String usernameChange, String passwdChange, String publicKeyId) {
        this.username = username;
        this.passwd = passwd;
        this.usernameChange = usernameChange;
        this.passwdChange = passwdChange;
        this.publicKeyId = publicKeyId;
    }
}
