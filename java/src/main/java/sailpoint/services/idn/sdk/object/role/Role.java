package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Role {

    @SerializedName("name")
    String name;

    @SerializedName("description")
    String description;

    @SerializedName("id")
    String id;

    @SerializedName("owner")
    String owner;

    @SerializedName("disabled")
    boolean disabled;

    @SerializedName("displayName")
    String displayName;

    @SerializedName("identityCount")
    int identityCount;

    @SerializedName("approvalSchemes")
    String approvalSchemes;

    @SerializedName("deniedCommentsRequired")
    boolean deniedCommentsRequired;

    @SerializedName("requestable")
    boolean requestable;

    @SerializedName("requestCommentsRequired")
    boolean requestCommentsRequired;

    @SerializedName("accessProfileIds")
    List<Object> accessProfileIds;

    @SerializedName("selector")
    Selector selector;



    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setSelector (Selector selector) {
        this.selector = selector;
    }

}


/* Leftover json from actual response

{
    "accessProfileIds": null,
    "selector": {
        "aliasList": [],
        "complexRoleCriterion": null,
        "entitlementIds": [],
        "ruleId": null,
        "sourceId": null,
        "type": "UNDEFINED",
        "valueMap": []
    }
}

 */