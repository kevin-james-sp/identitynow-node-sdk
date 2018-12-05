package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class MFAChallenge {

    @SerializedName("id")
    public String id;

    @SerializedName("text")
    public String text;

    @SerializedName("answer")
    public String answer;
}
