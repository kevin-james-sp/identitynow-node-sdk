package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MFAVerify {

    @SerializedName("id")
    public String id;

    @SerializedName("challenges")
    public List<MFAChallenge> challenges;

    public MFAVerify (String id, List<MFAChallenge> challenges) {
        this.id = id;
        this.challenges = challenges;
    }
}
