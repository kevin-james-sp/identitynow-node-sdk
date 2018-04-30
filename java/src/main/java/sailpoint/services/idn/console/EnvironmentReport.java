package sailpoint.services.idn.console;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;

public class EnvironmentReport {
	
	/**
	 * Take a clear text password like 'P@ssword4Me123' and redact to 'P************3'
	 * @param clearTextPassowrd
	 * @return
	 */
	private static String semiRedactPassword (String clearTextPassowrd) {
		if (null == clearTextPassowrd) return null;
		if (2 >= clearTextPassowrd.length()) return "*";
		String semiRedact = new String();
		semiRedact += clearTextPassowrd.charAt(0);
		for (int i =0; i< clearTextPassowrd.length()-2; i++) semiRedact += "*";
		semiRedact += clearTextPassowrd.charAt(clearTextPassowrd.length()-1);
		return semiRedact;
	}

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.INFO);
		
		System.out.println("");
		System.out.println("IdentityNow Services SDK - Environment Report Utility.");
		System.out.println("Copyright (C) 2018, SailPoint Technologies, Inc.");
		System.out.println("All Rights Reserved.");
		System.out.println("");
		
		// Report what org we will be talking to:
		
		ClientCredentials creds = EnvironmentCredentialer.getEnvironmentCredentials();
		
		System.out.println("org: "          + creds.getOrgName());
		System.out.println("url: "          + creds.getUserIntUrl());
		System.out.println("user: "         + creds.getOrgUser());
		System.out.println("password: "     + semiRedactPassword(creds.getOrgPass()));
		System.out.println("clientId: "     + creds.getClientId());
		System.out.println("clientSecret: " + semiRedactPassword(creds.getClientSecret()));
		System.out.println("");

	}

}
