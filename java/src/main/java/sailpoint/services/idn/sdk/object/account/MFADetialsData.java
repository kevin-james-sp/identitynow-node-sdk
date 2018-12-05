package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MFADetialsData {

    @SerializedName("challenges")
    public List<MFAChallenge> challenges;
}
