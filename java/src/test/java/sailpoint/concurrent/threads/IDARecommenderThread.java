package sailpoint.concurrent.threads;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import sailpoint.concurrent.objects.IDAMetrics;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.object.IAI.recommender.AccessItemRef;
import sailpoint.services.idn.sdk.object.IAI.recommender.RecommenderFields;
import sailpoint.services.idn.sdk.object.IAI.recommender.RequestElement;
import sailpoint.services.idn.sdk.object.IAI.recommender.Responses;
import sailpoint.services.idn.sdk.services.IAIService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class IDARecommenderThread implements Callable<IDAMetrics> {

	private RecommenderFields recommenderFields = new RecommenderFields();
	private ArrayList<RequestElement> requestElements = new ArrayList<>();
	private AccessItemRef accessItemRef;
	private String identityId;
	private List<String> accessItemIds;
	private String accessItemType;
	private boolean excludeInterpretations;
	private AccessItemRef accessItemRefs;
	private IAIService iaiService;
	private String token;

	private final static Logger log = LogManager.getLogger(IDARecommenderThread.class);

	//We'll run each test with only one type, and run a series of tests for each type.
	public IDARecommenderThread(String identityId, List<String> accessItemIds, String accessItemType, boolean excludeInterpretations, IAIService iaiService, String token){
		this.identityId = identityId;
		this.accessItemIds = accessItemIds;
		this.accessItemType = accessItemType;
		this.excludeInterpretations = excludeInterpretations;
		this.iaiService = iaiService;
		this.token = token;

		for(int i = 0; i < accessItemIds.size(); i++){
			accessItemRef = new AccessItemRef(accessItemIds.get(i), accessItemType);
			RequestElement thisElement = new RequestElement(identityId, accessItemRef);
			requestElements.add(thisElement);
		}

		recommenderFields.setExcludeInterpretations(excludeInterpretations);
		recommenderFields.setRequests(requestElements);

	}

/**
	 * Code to reset password or unlock account using "n" MFA.
	 * It loops through the available MFA drivers and randomly execute for the password reset or unlock account.
	 * @return true if the reset or unlock is successful. false otherwise
	 */

	@Override
	public IDAMetrics call() {
		Log4jUtils.boostrapLog4j(Level.DEBUG);

		long responseTime;
		int responseCode = -1;

		try {

			log.info("Executing call.");
			Call request = iaiService.recommendationRequest(token, "application/json;charset=utf-8", recommenderFields);
			log.debug(request.request().toString());
			log.debug(request.request().headers().toString());
			log.debug("Body " + request.request().body().contentLength());
			responseTime = System.currentTimeMillis();
			Response<Responses> response = request.execute();
			responseTime = System.currentTimeMillis() - responseTime;
			Responses responses = response.body();
			responseCode = response.code();

			if(response.isSuccessful()) {
				log.debug("Success!");
				log.debug(responses);
				return new IDAMetrics(true, responseTime, responses.getResponses(), responseCode);
			}
			else{
				log.debug("Failed!");
				log.debug(response.errorBody().string());
				log.error("Code: " + response.code());
				return new IDAMetrics(false, responseTime, responses.getResponses(), responseCode);
			}

			//return new IDAMetrics(successful, responseTime, recommendations);

		} catch (NullPointerException e) {
			log.error("Failed for identity: " + identityId + ". Response is missing required parameters.", e);
		} catch (IOException e) {
			log.error("Failed for identity: " + identityId + ". Could not get IAIService.", e);
		} catch (Exception e) {
			log.error("Failed for identity: " + identityId + ".", e);
		}

		responseTime = -1;
		return  new IDAMetrics(false, responseTime, null, responseCode);
	}
}