package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

public class RoleCriterionKey {
    @SerializedName("type")
    public String type;

    @SerializedName("property")
    public String property;

    @SerializedName("sourceId")
    public String sourceId;

    public RoleCriterionKey (String type, String property, String sourceId) {
        this.type = type;
        this.property = property;
        this.sourceId = sourceId;
    }
}
