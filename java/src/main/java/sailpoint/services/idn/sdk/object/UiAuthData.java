package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

public class UiAuthData {

	@SerializedName("encryption")
	String encryptionType;

	@SerializedName("publicKey")
	String publicKey;

	public UiAuthData() {
	}

	public UiAuthData(String encryptionType, String service) {

		this.encryptionType = encryptionType;
		this.service = service;
	}

	@SerializedName("service")
	String service;

	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPublicKey() { return publicKey; }

	public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

}

