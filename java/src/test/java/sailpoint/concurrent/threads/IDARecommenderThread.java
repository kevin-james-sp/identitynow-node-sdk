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
import sailpoint.services.idn.sdk.object.IAI.recommender.ResponseElement;
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
		boolean successful;
		try {

			log.info("Executing call.");
			responseTime = System.currentTimeMillis();
			Call request = iaiService.recommendationRequest(token, recommenderFields);
			log.info(request.request().toString());
			log.info(request.request().headers().toString());
			Response<ResponseElement> response = request.execute();
			log.info(response.toString());
			responseTime = System.currentTimeMillis() - responseTime;

			if(response.isSuccessful()) {
				successful = true;
				log.debug("Success!");
			}
			else{
				successful = false;
				log.debug("Failed!");
				log.debug(response.errorBody().string());
			}

			return new IDAMetrics(successful, responseTime);

		} catch (NullPointerException e) {
			log.error("Failed while for " + identityId + ". Server response is missing required parameters.", e);
		} catch (IOException e) {
			log.error("Failed for " + identityId + ". Could not get IAIService.", e);
		} catch (Exception e) {
			log.error("Failed for " + identityId + ".", e);
		}

		successful = false;
		responseTime = -1;
		return  new IDAMetrics(successful, responseTime);
	}
}