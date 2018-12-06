package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This is not used for now. But might be in future
 */
public class User {

    @SerializedName("id")
    public int id;

    @SerializedName("class")
    public String className;

    @SerializedName("alias")
    public String alias;

    @SerializedName("altAuthVia")
    public String altAuthVia;

    @SerializedName("altAuthViaIntegrationData")
    public String altAuthViaIntegrationData;

    @SerializedName("altAuthKey")
    public String altAuthKey;

    @SerializedName("altAuthKeyExpiration")
    public String altAuthKeyExpiration;

    @SerializedName("challenges")
    public List<MFAChallenge> challenges;

    @SerializedName("description")
    public String description;

    @SerializedName("email")
    public String email;

    @SerializedName("employeeNumber")
    public String employeeNumber;

    @SerializedName("enabled")
    public boolean enabled;

    @SerializedName("encryptionCheck")
    public String encryptionCheck;

    @SerializedName("encryptionKeyEncrypted")
    public String encryptionKeyEncrypted;

    @SerializedName("externalId")
    public String externalId;

    @SerializedName("kbaFailedAttempts")
    public int kbaFailedAttempts;

    @SerializedName("kbaLockoutExpiration")
    public String kbaLockoutExpiration;

    @SerializedName("keyPair")
    public String keyPair;

    @SerializedName("lastPasswdChange")
    public String lastPasswdChange;

    @SerializedName("name")
    public String name;

    @SerializedName("org")
    public Org org;

    @SerializedName("otpFailedAttempts")
    public int otpFailedAttempts;

    @SerializedName("otpLockoutExpiration")
    public String otpLockoutExpiration;

    @SerializedName("phone")
    public String phone;

    @SerializedName("phoneUpdated")
    public boolean phoneUpdated;

    @SerializedName("riskScore")
    public String riskScore;

    @SerializedName("status")
    public String status;

    @SerializedName("usageCertAttested")
    public boolean usageCertAttested;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("passwd")
	public String passwd;

    @SerializedName("passwordResetSinceLastLogin")
	public String passwordResetSinceLastLogin;

    @SerializedName("passwdResetKeyExpiration")
	public String passwdResetKeyExpiration;

    @SerializedName("passwdResetKey")
    public String passwdResetKey;
}
