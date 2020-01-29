package sailpoint.services.idn.sdk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility to extract the target Org's credentials from the environment if
 * they are available.  If not then this utility returns no credentials. 
 * @author adam.hampton
 *
 */
public class EnvironmentCredentialer {
	
	public final static Logger log = LogManager.getLogger(EnvironmentCredentialer.class);
	
	// Optional environment variable that specifies where the config file is located.
	public static final String IDENTITYNOW_SDK_CONF = "IDENTITYNOW_SDK_CONF";
	
	// The static credentials file lives hidden in a directory for the local user.
	public static final String credentialsFile = "/.idnSdk/sdkClient.conf";
	
	// The maximum number of KBA question responses to read from the environment.
	public static final int MAX_KBA_MAPPINGS = 5;
	
	public enum CredentialOrigin {
		// Credential was found in system environment or JVM property.
		ORIGIN_ENVIRONEMENT,
		
		// The credential was found in the local configuration file. 
		ORIGIN_FILE, 
		
		// The credential was entered by a user at the command prompt.
		ORIGIN_CONSOLE
	}
	
	/** 
	 * Load the lowest level defaults from the configuration file.  The file may
	 * or may not be present; gracefully handle passing back an empty map if there
	 * is no configuration file present. 
	 * @return
	 */
	private static Properties getConfigFileSettings () {
		
		Properties props = new Properties();
		
		// Allow the environment to specify an optional configuration file path. 
		String defaultCredsPath = System.getProperty("user.home") + "/" + credentialsFile;
		String filePath = System.getProperty("IDENTITYNOW_SDK_CONF", defaultCredsPath);
		
		File f = new File(filePath);
		if (f.exists()) try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			props.load(br);	
		} catch (IOException e) {
			System.err.println("Failure while loading IdentityNow SDK configuration from [" + filePath + "], " + e);
			e.printStackTrace();
			return props;
		}
		
		return props;
		
	}
	
	/**
	 * Access the JVM environment and extract the credentials to contact IdentityNow. 
	 * @return
	 */
	public static ClientCredentials getEnvironmentCredentials() {
		
		Properties cfProps = getConfigFileSettings();
		
		ClientCredentials creds = new ClientCredentials();
		
		// System properties override config file settings.
		creds.setOrgName     (System.getProperty("org",          cfProps.getProperty("org")));
		creds.setUserIntUrl  (System.getProperty("url",          cfProps.getProperty("url")));
		creds.setGatewayUrl  (System.getProperty("gwy",          cfProps.getProperty("gwy")));
		creds.setOrgUser     (System.getProperty("user",         cfProps.getProperty("user")));
		creds.setOrgPassHash (System.getProperty("passwordHash", cfProps.getProperty("passwordHash")));
		creds.setClientId    (System.getProperty("clientId",     cfProps.getProperty("clientId")));
		creds.setClientSecret(System.getProperty("clientSecret", cfProps.getProperty("clientSecret")));
		creds.setKbaDefault  (System.getProperty("kbaDefault",   cfProps.getProperty("kbaDefault")));
		creds.setKbaDefault  (System.getProperty("persAccToken", cfProps.getProperty("persAccToken")));
		
		// The 'password' property for some reason needs to be treated specially.
		String sysPassword = System.getProperty("password");
		String cfgPassword = cfProps.getProperty("password");
		if ((null != sysPassword) && (0 != sysPassword.length()) && (!sysPassword.trim().isEmpty())) {
			creds.setOrgPass(sysPassword);
		} else {
			creds.setOrgPass(cfgPassword);
		}
		
		// Search for individual KBA questions and answer texts in the environment.
		for (int i=0; i<MAX_KBA_MAPPINGS; i++) {
			
			String qKey = "kbaQ_" + (1+i);
			String aKey = "kbaA_" + (1+i);
			
			String kbaQText = System.getProperty(qKey, cfProps.getProperty(qKey));
			String kbaAText = System.getProperty(aKey, cfProps.getProperty(aKey));
			
			if (null != kbaQText) kbaQText = kbaQText.trim();
			if (null != kbaAText) kbaAText = kbaAText.trim();
			
			// Do we have a valid, populated question and answer text pair?
			if (	
					(null != kbaQText) && (0 != kbaQText.length()) &&
					(null != kbaAText) && (0 != kbaAText.length())
			) {
				creds.setKbaAnswer(kbaQText, kbaAText);
			}			
		}
		
		// TODO: Sanity check for any missing and/or required properties
		
		return creds;
	}
	

}
