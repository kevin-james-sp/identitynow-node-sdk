package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

public class RoleCriterionKey {
    @SerializedName("type")
    String type;

    @SerializedName("property")
    String property;

    @SerializedName("sourceId")
    String sourceId;

    public RoleCriterionKey (String type, String property, String sourceId) {
        this.type = type;
        this.property = property;
        this.sourceId = sourceId;
    }
}
