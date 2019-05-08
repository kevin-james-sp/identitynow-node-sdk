package sailpoint.services.idn.sdk.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sailpoint.services.idn.session.OkHttpUtils;
import sailpoint.services.idn.session.SessionBase;
import sailpoint.services.idn.session.SessionType;

import sailpoint.services.idn.sdk.object.*;

public class SearchIdentityService {
	
	SessionBase session = null;
	
	public final static Logger log = LogManager.getLogger(SearchIdentityService.class);
	
	// Identity Search.
	public static final String URL_SUFFIX_V2_SEARCH_IDENTITES = "/v2/search/identities";
	
	// Default to a limit of 50 records.
	public static final int DEFAULT_LIMIT = 50;
	
	public SearchIdentityService (SessionBase session) {
		
		// Validate we have the correct session type before allowing construction.
		// TODO: Decide if this should be pushed up into a super-class for Services.
		if (
			SessionType.SESSION_TYPE_API_ONLY.equals(session.getSessionType()) ||
			SessionType.SESSION_TYPE_API_WITH_USER.equals(session.getSessionType()) 
		) {
			// Valid session type, fall through.
		} else {
			throw new IllegalArgumentException("SearchIdentityService requires an API session.");
		}
		
		// TODO: Decide if services should auto-authenticate?
		// TODO: Decide if this should be pushed up into a super-class for Services.
		if (!session.isAuthenticated()) {
			log.warn("Session passed in is note yet authenticated; searches may fail!");
		}
		
		this.session = session;
		
	}

	/**
	 * Performs an search against the IdentityNow organization using Elastic search syntax.
	 * The search is executed against a single field, providing a simple interface for searches.
	 * @param field
	 * @param value
	 * @return
	 */
	public List<Identity> searchSingleField(String field, String value) {
		return search("query=" + field+":" + value);
	}
	
	/**
	 * Performs an search against the IdentityNow organization using Elastic search syntax.  
	 * TODO: Figure out paging and limits.
	 * @param elasticSearchQuery
	 * @return A list of the objects that match the query.
	 */
	public List<Identity> search(String elasticSearchQuery) {
		
		OkHttpUtils okClient = new OkHttpUtils(session); 
		
		OkHttpClient.Builder cliBuilder = okClient.getClientBuilder();
		String url = session.getApiGatewayUrl() + URL_SUFFIX_V2_SEARCH_IDENTITES;
		
		Request.Builder reqBuilder = okClient.getRequestBuilder(url);
		reqBuilder.addHeader("Accept", "application/json; q=0.5");
		reqBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
		
		// Searches are POSTed with a ?query form parameter with the search string.
		// Looks something like: ?limit=1000&query=" + ${fieldName} +":" + ${fieldValue}
		FormBody.Builder formBuilder = new FormBody.Builder();
		// Add a default limit.  TODO: Make this configurable?
		formBuilder.add("limit", "" + DEFAULT_LIMIT);
		formBuilder.addEncoded("query", elasticSearchQuery);
		
		RequestBody formBody = formBuilder.build();
		
		// reqBuilder.post(formBody);
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
		reqBuilder.post(RequestBody.create(mediaType, "?limit=50&query=name=11111"));
		
		// TODO: Left off here! Figure out how to get the form payload to actually post here.
		
		
		Response response = okClient.callWithRetires(cliBuilder.build(), reqBuilder.build());
		
		// Parse the response json that comes back.  It should return a JSON array.
		Gson gson = new Gson();
		Identity[] identityArray = null;
		try {
			identityArray = gson.fromJson(response.body().string(), Identity[].class);
		} catch (JsonSyntaxException e) {
			log.error("Failure parsing JSON resposne for Identity Search", e);
		} catch (IOException e) {
			log.error("IOException parsing JSON resposne for Identity Search", e);
		} 
		
		return Arrays.asList(identityArray);
	}
	
	
	public String getPublicCount () {
		return "TBD"; // TODO: THIS!
	}
	

}
