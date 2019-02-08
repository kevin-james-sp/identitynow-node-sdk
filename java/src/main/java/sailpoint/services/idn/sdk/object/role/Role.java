package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Role {

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("id")
    public String id;

    @SerializedName("owner")
    public String owner;

    @SerializedName("disabled")
    public boolean disabled;

    @SerializedName("displayName")
    public String displayName;

    @SerializedName("identityCount")
    public int identityCount;

    @SerializedName("approvalSchemes")
    public String approvalSchemes;

    @SerializedName("deniedCommentsRequired")
    public boolean deniedCommentsRequired;

    @SerializedName("requestable")
    public boolean requestable;

    @SerializedName("requestCommentsRequired")
    public boolean requestCommentsRequired;

    @SerializedName("accessProfileIds")
    public List<Object> accessProfileIds;

    @SerializedName("selector")
    public Selector selector;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}