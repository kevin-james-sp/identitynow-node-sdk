package sailpoint.services.idn.sdk.scaffolding;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;

import okhttp3.OkHttpClient;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.Identity;
import sailpoint.services.idn.session.ApiSession;
import sailpoint.services.idn.session.OkHttpUtils;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

/**
 * Scaffolding infrastructure to support accessing the Search service.
 * @author adam.hampton
 *
 */
public class GetSSOSettingsScaffolding {

	public static void main(String[] args) {

		Log4jUtils.boostrapLog4j(Level.DEBUG);

		try {
			
			IdentityNowService ids = new IdentityNowService(
					EnvironmentCredentialer.getEnvironmentCredentials()
			);
			System.out.println("Authenticating ...");
			ApiSession session = (ApiSession) ids.createSession(SessionType.SESSION_TYPE_API_WITH_USER);
			System.out.println("getUniqueId: " + session.getUniqueId());
			
			String getResponse = session.doApiGet("/cc/api/org/setSSOSettings");
			
			Map<String,String> formData = new TreeMap<String,String>();
			formData.put("enableRemoteIdp", "true");
			formData.put("ssoIdpAllowDirectLogin", "true");
			/*
			ssoIdpEntityID
			ssoIdpLoginUrl
			ssoIdpLoginRedirectUrl
			ssoIdpLogoutUrl
			ssoIdpMappingAttribute
			ssoIdpNameIdFormat
			ssoIdpRequestBinding
			ssoIdpRequestedAuthnContext
			ssoIdpExcludeReqAuthnContext
			certificateName
			certificateExpirationDate
			certificateMissing
			signingCertificate
			enableHostedSp
			config
			*/
			
			String putResponse = session.doApiPost("/cc/api/org/setSSOSettings", formData);
			
			System.out.println("putResponse: " + putResponse);
			
			// SearchService searchService = ids.getSearchService();
			// List<Identity> idList = searchService.searchIdentities(50, 0, "id=99999").execute().body();

		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
