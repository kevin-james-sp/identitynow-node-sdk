package sailpoint.services.idn.sdk.object.accessrequest;

import com.google.gson.annotations.SerializedName;

public class RequestableObject {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("type")
    public String type;

    @SerializedName("email")
    public String email;

    @SerializedName("comment")
    public String comment;

    @SerializedName("requestStatus")
    public String requestStatus;

    @SerializedName("identityRequestId")
    public String identityRequestId;

    @SerializedName("requestCommentsRequired")
    public boolean requestCommentsRequired;

    @SerializedName("created")
    public String created;

    @SerializedName("modified")
    public String modified;

}
