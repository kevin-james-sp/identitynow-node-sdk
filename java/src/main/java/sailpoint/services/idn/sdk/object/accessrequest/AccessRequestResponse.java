package sailpoint.services.idn.sdk.object.accessrequest;

import com.google.gson.annotations.SerializedName;

public class AccessRequestResponse {

    @SerializedName("error")
    public String error;

    @SerializedName("error_description")
    public String errorDescription;
}
