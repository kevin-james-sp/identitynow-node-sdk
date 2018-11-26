package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

public class RoleCriterion {
    @SerializedName("operation")
    public String operation;

    @SerializedName("key")
    public RoleCriterionKey key;

    @SerializedName("value")
    public String value;

    public RoleCriterion(String operation, RoleCriterionKey key, String value) {
        this.operation = operation;
        this.key = key;
        this.value = value;
    }
}