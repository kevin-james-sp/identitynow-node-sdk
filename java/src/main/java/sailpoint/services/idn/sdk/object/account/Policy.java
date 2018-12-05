package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class Policy {

    @SerializedName("minSpecial")
    public String minSpecial;

    @SerializedName("validateAgainstAccountName")
    public boolean validateAgainstAccountName;

    @SerializedName("maxRepeatedChars")
    public String maxRepeatedChars;

    @SerializedName("minUpper")
    public String minUpper;

    @SerializedName("validateAgainstAccountId")
    public boolean validateAgainstAccountId;

    @SerializedName("useAccountAttributes")
    public boolean useAccountAttributes;

    @SerializedName("minLower")
    public String minLower;

    @SerializedName("maxLength")
    public String maxLength;

    @SerializedName("minNumeric")
    public String minNumeric;

    @SerializedName("minLength")
    public String minLength;

    @SerializedName("minAlpha")
    public String minAlpha;

    @SerializedName("useIdentityAttributes")
    public boolean useIdentityAttributes;

    @SerializedName("minCharacterTypes")
    public String minCharacterTypes;

    @SerializedName("accountNameMinWordLength")
    public String accountNameMinWordLength;

    @SerializedName("accountIdMinWordLength")
    public String accountIdMinWordLength;
}
