package sailpoint.services.idn.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility to extract the target Org's credentials from the environment if
 * they are available.  If not then this utility returns no credentials. 
 * @author adam.hampton
 *
 */
public class EnvironmentCredentialer {
	
	public final static Logger log = LogManager.getLogger(EnvironmentCredentialer.class);
	
	// The static credentials file lives hidden in a directory for the local user.
	public static final String credentialsFile = "/.idnSdk/sdkClient.conf";
	
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
		
		String filePath = System.getProperty("user.home") + "/" + credentialsFile; 
		
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
		creds.setOrgUser     (System.getProperty("user",         cfProps.getProperty("user")));
		creds.setOrgPass     (System.getProperty("password",     cfProps.getProperty("password")));
		creds.setOrgPassHash (System.getProperty("passwordHash", cfProps.getProperty("passwordHash")));
		creds.setClientId    (System.getProperty("clientId",     cfProps.getProperty("clientId")));
		creds.setClientSecret(System.getProperty("clientSecret", cfProps.getProperty("clientSecret")));
		
		// TODO: Sanity check for any missing and/or required properties
		
		return creds;
	}
	

}
