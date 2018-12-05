package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class JPTResult {

    @SerializedName("result")
    public String result;

    @SerializedName("JPT")
    public String JPT;

    @SerializedName("error_code")
    public String error_code;

    @SerializedName("exception_message")
    public String exception_message;

}
