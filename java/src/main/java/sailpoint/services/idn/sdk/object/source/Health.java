package sailpoint.services.idn.sdk.object.source;

import com.google.gson.annotations.SerializedName;

public class Health {
    @SerializedName("hostname")
    public String hostname;

    @SerializedName("lastSeen")
    public String lastSeen;

    @SerializedName("org")
    public String org;

    @SerializedName("healthy")
    public Boolean healthy;

    @SerializedName("lastChanged")
    public String lastChanged;

    @SerializedName("isAuthoritative")
    public String isAuthoritative;

    @SerializedName("externalId")
    public String externalId;

    @SerializedName("id")
    public String id;

    @SerializedName("type")
    public String type;

    @SerializedName("status")
    public String status;

    @SerializedName("since")
    public Long since;

    @SerializedName("iqServiceVersion")
    public String iqServiceVersion;

    @SerializedName("name")
    public String name;

    @SerializedName("isCluster")
    public String isCluster;
}
