package sailpoint.services.idn.sdk.scaffolding;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.PersonalAccessTokenRequest;
import sailpoint.services.idn.sdk.object.PersonalAccessTokenResponse;
import sailpoint.services.idn.session.OkHttpUtils;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

public class PersonalAccessTokenScaffolding {
	
	public final static Logger log = LogManager.getLogger(PersonalAccessTokenScaffolding.class);

	public PersonalAccessTokenScaffolding() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		Log4jUtils.boostrapLog4j(Level.DEBUG);

		try {
			
			IdentityNowService ids = new IdentityNowService(
					EnvironmentCredentialer.getEnvironmentCredentials()
			);
			
			UserInterfaceSession uiSession = (UserInterfaceSession) 
					ids.createSession(SessionType.SESSION_TYPE_UI_USER_STRONG_AUTHN);
			
			System.out.println("getUniqueId: " + uiSession.getUniqueId());
			
			OkHttpClient client = uiSession.getClient();
			
			String patApiSuffix = "/beta/personal-access-tokens";
			
			HashMap<String,String> apiHeadersMap = new HashMap<String,String>();
			apiHeadersMap.put("Authorization", "Bearer " + uiSession.getAccessToken());
			
			PersonalAccessTokenRequest patReq = new PersonalAccessTokenRequest();
			patReq.setName("Requested by IdentityNow Services SDK - " + System.currentTimeMillis());
			
			String apiUrl = uiSession.getApiGatewayUrl() + patApiSuffix;
			
			Request.Builder builder = new Request.Builder();
			OkHttpUtils.appendHeaders(builder, OkHttpUtils.getDefaultHeaders());
			if ((null != apiHeadersMap) && (!apiHeadersMap.isEmpty())) {
				OkHttpUtils.appendHeaders(builder, apiHeadersMap);
			}
			builder.addHeader("User-Agent", OkHttpUtils.getUserAgent());
			/*
			if ((null != optionalCookies) && (!optionalCookies.isEmpty())) {
				for (HttpCookie cookie: optionalCookies) {
					builder.addHeader("Cookie", getCookieString(cookie));
				}
			}
			*/
			
			MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
			
//			ObjectMapper om = new ObjectMapper();
			
			Gson gson = new Gson();
			
			String jsonPayload = gson.toJson(patReq);
			
			RequestBody body = RequestBody.create(mediaType, jsonPayload);
			
			builder.url(apiUrl);
			builder.post(body);
			Request request = builder.build();

			try (Response response = OkHttpUtils.callWithRetires(client, request)) {
				if (!response.isSuccessful()) {
					log.error(response.code() + " while calling " + response.request().url().toString());
				}
				String responseJson = response.body().string();
				response.body().close();
				// Spare the expensive string concat if we can:
				if (log.isDebugEnabled()) {
					log.debug(response.request().url().toString() + ": " + responseJson);
				}
				PersonalAccessTokenResponse patRsp = gson.fromJson(responseJson, PersonalAccessTokenResponse.class);
				log.debug("Personal Access Token secret:" + patRsp.getSecret());
			} catch (IOException e) {
				log.error("Failure while calling " + patApiSuffix, e);
			}
			
		} catch (IOException e) {
			log.error("Failure interacting with IdentityNow", e);
		} finally {
			
		}

	}

}
