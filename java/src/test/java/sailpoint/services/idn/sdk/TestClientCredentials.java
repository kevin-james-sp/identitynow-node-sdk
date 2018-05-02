package sailpoint.services.idn.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestClientCredentials {
	
	@Test
	public void testKbaMapping() {
		
		// Base case: no default mapping.
		ClientCredentials cc = new ClientCredentials();
		
		String fcQText = "favorite city";
		String hsQText = "what high school";
		String dfaText = "rubber baby buggy bumpers";
		
		String baseCase = cc.getKbaAnswer(fcQText);
		assertNull("The answer to 'favorite city' should be null by default.", baseCase);
		
		cc.setKbaAnswer(fcQText, "Boulder");
		String mapCasePresent = cc.getKbaAnswer(fcQText);
		assertNotNull("The answer to 'favorite city' should populated after setting.", mapCasePresent);
		
		String highSchoolAbsent = cc.getKbaAnswer(hsQText);
		assertNull("The answer to 'high school' should be null by default.", highSchoolAbsent);
		
		cc.setKbaAnswer(hsQText, "Beverly Hills High School");
		String highSchoolPresent = cc.getKbaAnswer(hsQText);
		assertNotNull("The answer to 'high school' should populated after setting.", highSchoolPresent);
		
		cc.setKbaDefault(dfaText);
		String aDefault = cc.getKbaDefault();
		assertNotNull("The default should work after setting.", aDefault);
		assertTrue("The default shoudl match what was assigned.", dfaText.equals(aDefault));
	}

}
