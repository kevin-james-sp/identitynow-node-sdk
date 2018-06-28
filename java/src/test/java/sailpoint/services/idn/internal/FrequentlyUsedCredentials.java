package sailpoint.services.idn.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sailpoint.services.idn.sdk.ClientCredentials;

public class FrequentlyUsedCredentials {
	
	private static FrequentlyUsedCredentials _singleton = null;
	
	private HashMap<String, ClientCredentials> orgCredsMap = null;
	
	// Singleton reference accessor method.
	public static synchronized FrequentlyUsedCredentials getInstance() {
		if (null == _singleton) {
			_singleton = new FrequentlyUsedCredentials();
		}
		return _singleton;
	}
	
	private FrequentlyUsedCredentials() {
		
		orgCredsMap = new HashMap<String, ClientCredentials>();
		
		ClientCredentials creds = new ClientCredentials();
		creds.setGatewayUrl("https://perflab-05191440.api.cloud.sailpoint.com");
		creds.setOrgName("perflab-05191440");
		creds.setOrgUser("support");
		creds.setOrgPass("2thecloud");
		creds.setClientId("jifYvTzUOHD7aLvs");
		creds.setClientSecret("AZm9WhqGGqEaGH3Ze1atgGYHEACjsnOe");
		orgCredsMap.put(creds.getOrgName(), creds);
		
		creds = new ClientCredentials();
		creds.setGatewayUrl("https://perflab-05121458.api.cloud.sailpoint.com");
		creds.setOrgName("perflab-05121458");
		creds.setOrgUser("support");
		creds.setOrgPass("2thecloud");
		creds.setClientId("wYyxbDcSbJdohcm0");
		creds.setClientSecret("DS2DiG4Al1EI8iPfgIxFvpn5xVMMJvhF");
		orgCredsMap.put(creds.getOrgName(), creds);
		
		creds = new ClientCredentials();
		creds.setGatewayUrl("https://perflab-05121107.api.cloud.sailpoint.com");
		creds.setOrgName("perflab-05121107");
		creds.setOrgUser("support");
		creds.setOrgPass("4TheCloud!");
		creds.setClientId("ASxcFBnJCfdeTHH8");
		creds.setClientSecret("kfxAuQ6gF7QcTma6GskqD2E0nKC8w7qy");
		orgCredsMap.put(creds.getOrgName(), creds);
		
		creds = new ClientCredentials();
		creds.setGatewayUrl("https://perflab-09072140.api.cloud.sailpoint.com");
		creds.setOrgName("perflab-09072140");
		creds.setOrgUser("support");
		creds.setOrgPass("2thecloud");
		creds.setClientId("ffzfZNWBt0dF7CIc");
		creds.setClientSecret("cCvKMhKKICc2siEKmXj2xgDxXz52DplC");
		orgCredsMap.put(creds.getOrgName(), creds);
		
	}
	
	public ClientCredentials getOrgCredentials (String orgName) {
		return orgCredsMap.get(orgName);
	}
	
	/**
	 * Returns a List<ClientCredentials> for all known orgs.  This is used
	 * for regression tests that are non-destructive and read-only and that
	 * need to touch a number of different orgs for breadth of coverage.
	 * @return
	 */
	public List<ClientCredentials> getAllTestCredentials() {
		ArrayList<ClientCredentials> credsList = new ArrayList<ClientCredentials>();
		for (String orgName : orgCredsMap.keySet()) {
			credsList.add(orgCredsMap.get(orgName));
		}
		return credsList;
	}

}
