package sailpoint.services.idn.sdk.object.source;

import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("id")
    public String id;

    @SerializedName("version")
    public Long version;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("owner")
    public Owner owner;

    @SerializedName("lastUpdated")
    public String lastUpdated;

    @SerializedName("scriptName")
    public String scriptName;

    @SerializedName("definitionName")
    public String definitionName;

    @SerializedName("appCount")
    public Long appCount;

    @SerializedName("userCount")
    public Long userCount;

    @SerializedName("sourceConnected")
    public Boolean sourceConnected;

    @SerializedName("applicationTemplate")
    public String applicationTemplate;

    @SerializedName("sourceConnectorName")
    public String sourceConnectorName;

    @SerializedName("sourceDirectConnect")
    public String sourceDirectConnect;

    @SerializedName("sourceReleaseStatus")
    public String sourceReleaseStatus;

    @SerializedName("supportsEntitlementAggregation")
    public String supportsEntitlementAggregation;

    @SerializedName("externalId")
    public String externalId;

    @SerializedName("icon")
    public String icon;

    @SerializedName("health")
    public Health health;

    @SerializedName("sourceType")
    public String sourceType;

    @SerializedName("useForAuthentication")
    public Boolean useForAuthentication;

    @SerializedName("useForAccounts")
    public Boolean useForAccounts;

    @SerializedName("useForProvisioning")
    public Boolean useForProvisioning;

    @SerializedName("useForPasswordManagement")
    public Boolean useForPasswordManagement;

    @SerializedName("iqServiceDownloadUrl")
    public String iqServiceDownloadUrl;

    @SerializedName("connectorCluster")
    public String connectorCluster;

    @SerializedName("connectorClusterGmtOffset")
    public String connectorClusterGmtOffset;
    
}
