package sailpoint.services.idn.sdk.object.accessrequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccessRequest {

    @SerializedName("requestedFor")
    public List<String> requestedFor;

    @SerializedName("requestedItems")
    public List<RequestableObject> requestedItems;

    public AccessRequest (List<String> requestedFor, List<RequestableObject> requestedItems) {
        this.requestedFor = requestedFor;
        this.requestedItems = requestedItems;
    }
}
