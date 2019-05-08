package sailpoint.services.idn.sdk.scaffolding;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Level;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.object.Identity;
import sailpoint.services.idn.sdk.services.SearchIdentityService;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionFactory;

/**
 * Scaffolding infrastructure to support accessing the Search service.
 * @author adam.hampton
 *
 */
public class SearchScaffolding {

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.DEBUG);
		
		SessionBase session = SessionFactory.createApiSession();
		try {
			session.open();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		SearchIdentityService srchIdSvc = new SearchIdentityService(session);
		
		List<Identity> idList = srchIdSvc.search("id=99999");
		
		try {
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	

}
