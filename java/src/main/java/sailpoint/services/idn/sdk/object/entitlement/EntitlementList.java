package sailpoint.services.idn.sdk.object.entitlement;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntitlementList {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<Entitlement> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Entitlement> getItems() {
        return items;
    }

    public void setItems(List<Entitlement> items) {
        this.items = items;
    }
}
