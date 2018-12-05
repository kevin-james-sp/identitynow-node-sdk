package sailpoint.services.idn.util;

import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class PasswordUtil {

    /**
     * Encrypts a plain text with the specified RSA public key, given the
     * contents of the public key.
     *
     * @param plainText the text to encrypt
     * @param cert contents of the public key
     * @return the encrypted plain text
     */
    public static String getPassthroughHash(String plainText, String cert){
        String hash = "";
        try{
            cert = cert.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----","");//.replace("\r","").replace("\n","")
            byte[] decodedBytes = Base64.decodeBase64(cert.getBytes("UTF-8"));
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pk = kf.generatePublic(publicKeySpec);
            hash = encodeRSAString(plainText ,pk);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return hash;
    }

    /**
     * Encrypts a string with the specified RSA public key.
     *
     * @param plainText the string to encrypt
     * @param pubKey the public key
     * @return the encrypted string
     */
    public static String encodeRSAString(String plainText, Key pubKey) {
        String returnString = "";
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey); //pubKey stored earlier
            returnString = new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes())),"UTF-8");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return returnString;
    }

    /**
     * Encrypts a string with sha256
     * @param plainText the string to encrypt
     * @return the encrypted string
     */
    public static String encodeSha256String (String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }
}
