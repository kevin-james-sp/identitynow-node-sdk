package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

public class RoleCriterion {
    @SerializedName("operation")
    String operation;

    @SerializedName("key")
    RoleCriterionKey key;

    @SerializedName("value")
    String value;

    public RoleCriterion(String operation, RoleCriterionKey key, String value) {
        this.operation = operation;
        this.key = key;
        this.value = value;
    }
}