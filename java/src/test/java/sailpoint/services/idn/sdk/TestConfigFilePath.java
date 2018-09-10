package sailpoint.services.idn.sdk;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestConfigFilePath {
	
	@Test
	public void testCustomConfigFileLocation() {
		
		String prefix = "sdkClient-conf-";
		String suffix = ".tmp";
	    
		File tempFile = null;
		try {
			tempFile = File.createTempFile(prefix, suffix);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Test requires a temporary file to move forward.");
		}
		
		// Remove ('unlink') the file on process exit.
		tempFile.deleteOnExit();
		
		// Populate our temp file with some contents.
		try (
			FileWriter fw = new FileWriter(tempFile);
			BufferedWriter bw = new BufferedWriter(fw);
		) {
			bw.write("# This is a configuration file for the Chandlery / IdentityNow Services SDK. \n");
			bw.write("# Values are stored in simple key=value format. \n");
			bw.write("\n");
			bw.write("# Values are stored in simple key=value format. \n");
			
			bw.write("org=perflab-09072140\n");
			bw.write("url=https://dev01-useast1.cloud.sailpoint.com/perflab-09072140/\n");
			bw.write("user=support\n");
			bw.write("password=2thecloud\n"); 
			bw.write("clientId=ffzfZNWBt0dF7CIc\n");
			bw.write("clientSecret=cCvKMhKKICc2siEKmXj2xgDxXz52DplC\n");
			bw.write("\n");	
			bw.write("# These are only necessary if we want to automate strong-AuthN as a single user. \n");
			bw.write("kbaDefault=test \n");
			bw.write("kbaQ_1=best window manager \n");
			bw.write("kbaA_1=GNome \n");
			bw.write("kbaQ_2=favorite operating system \n");
			bw.write("kbaA_2=NetBSD \n");
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		System.out.println("Using configuration file location: " + tempFile.getPath());
		System.setProperty(EnvironmentCredentialer.IDENTITYNOW_SDK_CONF, tempFile.getPath()); 
		
		ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();

		// Compare some of the fields to ensure they are sane.
		assertEquals(envCreds.getOrgName(), "perflab-09072140");
		assertEquals(envCreds.getClientSecret(), "cCvKMhKKICc2siEKmXj2xgDxXz52DplC");
		
	}

}
