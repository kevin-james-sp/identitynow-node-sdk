package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class MFADetails {

    @SerializedName("label")
    public String label;

    @SerializedName("description")
    public String description;

    @SerializedName("type")
    public String type;

    @SerializedName("method")
    public String method;

    @SerializedName("available")
    public boolean available;

    @SerializedName("verified")
    public boolean verified;

    @SerializedName("JPT")
    public String JPT;

    @SerializedName("data")
    public MFADetialsData data;
}
