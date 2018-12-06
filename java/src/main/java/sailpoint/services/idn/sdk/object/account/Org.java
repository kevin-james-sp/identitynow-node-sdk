package sailpoint.services.idn.sdk.object.account;

import com.google.gson.annotations.SerializedName;

public class Org {

    @SerializedName("id")
    public int id;

    @SerializedName("class")
    public String className;

    @SerializedName("encryptionKey")
    public String encryptionKey;

    @SerializedName("encryptionKeyId")
    public String encryptionKeyId;

}
