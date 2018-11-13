package sailpoint.services.idn.sdk.object.entitlement;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntitlementList {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<Entitlement> items;
}
