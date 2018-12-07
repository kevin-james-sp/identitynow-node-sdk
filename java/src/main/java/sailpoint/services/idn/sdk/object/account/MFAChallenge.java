package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class MFAChallenge {

    @SerializedName("id")
    public String id;

    @SerializedName("class")
    public String className;

    @SerializedName("text")
    public String text;

    @SerializedName("answer")
    public String answer;

    public MFAChallenge (String id, String answer) {
        this.id = id;
        this.answer = answer;
    }
}
