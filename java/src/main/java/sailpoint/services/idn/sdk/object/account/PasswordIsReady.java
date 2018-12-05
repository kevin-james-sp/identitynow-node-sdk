package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PasswordIsReady {

    @SerializedName("ready")
    public boolean ready;

    @SerializedName("JPT")
    public String JPT;

    @SerializedName("mfa")
    public List<MFADetails> mfa;

}
