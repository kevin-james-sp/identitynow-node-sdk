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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public Long getAppCount() {
        return appCount;
    }

    public void setAppCount(Long appCount) {
        this.appCount = appCount;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Boolean getSourceConnected() {
        return sourceConnected;
    }

    public void setSourceConnected(Boolean sourceConnected) {
        this.sourceConnected = sourceConnected;
    }

    public String getApplicationTemplate() {
        return applicationTemplate;
    }

    public void setApplicationTemplate(String applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    public String getSourceConnectorName() {
        return sourceConnectorName;
    }

    public void setSourceConnectorName(String sourceConnectorName) {
        this.sourceConnectorName = sourceConnectorName;
    }

    public String getSourceDirectConnect() {
        return sourceDirectConnect;
    }

    public void setSourceDirectConnect(String sourceDirectConnect) {
        this.sourceDirectConnect = sourceDirectConnect;
    }

    public String getSourceReleaseStatus() {
        return sourceReleaseStatus;
    }

    public void setSourceReleaseStatus(String sourceReleaseStatus) {
        this.sourceReleaseStatus = sourceReleaseStatus;
    }

    public String getSupportsEntitlementAggregation() {
        return supportsEntitlementAggregation;
    }

    public void setSupportsEntitlementAggregation(String supportsEntitlementAggregation) {
        this.supportsEntitlementAggregation = supportsEntitlementAggregation;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Boolean getUseForAuthentication() {
        return useForAuthentication;
    }

    public void setUseForAuthentication(Boolean useForAuthentication) {
        this.useForAuthentication = useForAuthentication;
    }

    public Boolean getUseForAccounts() {
        return useForAccounts;
    }

    public void setUseForAccounts(Boolean useForAccounts) {
        this.useForAccounts = useForAccounts;
    }

    public Boolean getUseForProvisioning() {
        return useForProvisioning;
    }

    public void setUseForProvisioning(Boolean useForProvisioning) {
        this.useForProvisioning = useForProvisioning;
    }

    public Boolean getUseForPasswordManagement() {
        return useForPasswordManagement;
    }

    public void setUseForPasswordManagement(Boolean useForPasswordManagement) {
        this.useForPasswordManagement = useForPasswordManagement;
    }

    public String getIqServiceDownloadUrl() {
        return iqServiceDownloadUrl;
    }

    public void setIqServiceDownloadUrl(String iqServiceDownloadUrl) {
        this.iqServiceDownloadUrl = iqServiceDownloadUrl;
    }

    public String getConnectorCluster() {
        return connectorCluster;
    }

    public void setConnectorCluster(String connectorCluster) {
        this.connectorCluster = connectorCluster;
    }

    public String getConnectorClusterGmtOffset() {
        return connectorClusterGmtOffset;
    }

    public void setConnectorClusterGmtOffset(String connectorClusterGmtOffset) {
        this.connectorClusterGmtOffset = connectorClusterGmtOffset;
    }
    
}
