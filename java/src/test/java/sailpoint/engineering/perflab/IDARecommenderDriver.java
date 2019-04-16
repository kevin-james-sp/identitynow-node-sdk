package sailpoint.engineering.perflab;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sailpoint.concurrent.objects.IDAMetrics;
import sailpoint.concurrent.threads.IDARecommenderThread;
import sailpoint.services.idn.console.Log4jUtils;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.object.IAI.Oauth.AccessToken;
import sailpoint.services.idn.sdk.services.IAIService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IDARecommenderDriver {

	public final static Logger log = LogManager.getLogger(IDARecommenderDriver.class);

	private static String clientId = "35872355-6096-4b21-b0b2-7d9b814ff790";
	private static String key = "c62fb62724bc46a080d313edc7ef5131c3476513cbb417e01d18806eeca0e30e";
	private static String dbUrl = "jdbc:mysql://iai-perflab-iiqdb.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com:3306/identityiq_72?serverTimezone=UTC&useCursorFetch=true&zeroDateTimeBehavior=convertToNull";
	private static String token;
	private static AccessToken accessToken;
	private static String username = "identityiq";
	private static String password = "identityiq";

	public static void main(String[] args){

		//Bootstrap logger, and vars
		Log4jUtils.boostrapLog4j(Level.ALL);

		double batchSize = Double.parseDouble(args[0]);
		double numRequests = Double.parseDouble(args[1]);
		boolean excludeInterpretations = Boolean.parseBoolean(args[2]);
		int threadCount = Integer.parseInt(args[3]);
		boolean continueWithError = false;
		int successfulCalls;
		int avgResponseTime;
		long startTime;
		long duration;
		LinkedList<String> identityIds = new LinkedList<>();
		LinkedList<String> accessIds = new LinkedList<>();
		LinkedList<IDARecommenderThread> workQueue = new LinkedList<>();
		LinkedList<String> batch = new LinkedList<>();
		List<Future<IDAMetrics>> metricsList;


		//Get url, iaiservice, and executor
		ClientCredentials clientCredentials = EnvironmentCredentialer.getEnvironmentCredentials();
		IAIService iaiService = getIAIService("https://" + clientCredentials.getOrgName() + ".api.cloud.sailpoint.com/");

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);

		//connect to database, and pull back required info
		try {
			//make driver register for classloader
			Class.forName("com.mysql.jdbc.Driver");

			//Get a list of Identity Ids
			Connection connection = DriverManager.getConnection(dbUrl, username, password);


			String query = "SELECT id FROM spt_identity;";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next())
				identityIds.push(resultSet.getString(1));

			resultSet.close();

			//Get a list of entitlement ids
			query = "SELECT id FROM spt_identity_entitlement;";
			resultSet = statement.executeQuery(query);

			while (resultSet.next())
				accessIds.push(resultSet.getString(1));

			//cleanup database connections
			connection.close();
			statement.close();
			resultSet.close();

			//Get JWT token
			accessToken = iaiService.refreshToken(Credentials.basic(clientId, key)).execute().body();
			token = "Bearer " + accessToken.getAccess_token();
			log.info("Token is: " + token);

		} catch(SQLException e) {
			log.error("Database connection problem: ", e);
		} catch(ClassNotFoundException e) {
			log.error("Unable to find driver class", e);
		} catch (IOException e){
			log.error("Unable to get JWT Token.", e);
		}

		//Build work queue and pass information to threads. Catch configuration errors and continue if possible.
		String currentAccessId;
		for(int i = 0; i < numRequests; i++){
			batch = new LinkedList<>();
			try{
				for(int j = 0; j < batchSize; j++){
					currentAccessId = accessIds.pop();
					batch.push(currentAccessId);
					accessIds.push(currentAccessId);
					if(continueWithError)
						break;
				}
			} catch(NoSuchElementException e){
				log.error("------------------------------------------------------------------------------------------------------------------");
				log.error("The list of access items has been depleted before the test coud satisfy the batch size and thread requirements." +
						"Will attempt to continue the test with existing threads." , e);
				log.error("------------------------------------------------------------------------------------------------------------------");
				continueWithError = true;
			}

			//So long as we are sending less than 250k requests, it is okay to pop identity Ids. This will need to be reworked if we want to send more.
			workQueue.push(new IDARecommenderThread(identityIds.pop(), batch, "ENTITLEMENT", excludeInterpretations, iaiService, token));

			if(continueWithError)
				break;
		}

		try{
			log.info("workQueue size: " + workQueue.size());

			//Test complete, process results and display them to console
			processResultList(executor.invokeAll(workQueue));
			executor.shutdown();
		} catch(InterruptedException e){
			log.error("The executor has been interrupted.", e);
		}

	}

	//Copied and modified from identityNowService to avoid placing keys outside of test package.
	public static IAIService getIAIService (String url) {

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		OkHttpClient client = clientBuilder.build();
		String basicCredentials = Credentials.basic(clientId, key);
		AccessToken accessToken = null;

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl( url )
				.addConverterFactory(GsonConverterFactory.create())
				.client( client )
				.build();

		return retrofit.create( IAIService.class );
	}

	/**
	 * This is a helper method to count the results of execution, and return the number of successful executions, and their times in miliseconds.
	 * @param metricsList The list of futures to count.
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 * @throws ExecutionException if the computation threw an exception
	 * @throws NullPointerException If the resultList is null
	 */
	private static void processResultList(List<Future<IDAMetrics>> metricsList) {
		long numSuccess = 0;
		long numFailed = 0;
		long avgTime;
		long maxTime = 0;
		long minTime = Integer.MAX_VALUE;
		long sum = 0;
		long currentResponseTime;
		IDAMetrics currentMetric;
		try{
			for(Future<IDAMetrics> metric : metricsList){
				currentMetric = metric.get();

				if(currentMetric.isSuccessful()){
					numSuccess++;
					currentResponseTime = currentMetric.getResponseTime();
					sum += currentResponseTime;
					if(currentResponseTime > maxTime)
						maxTime = currentResponseTime;
					if(currentResponseTime < minTime)
						minTime = currentResponseTime;
				}
				else{
					numFailed++;
					break;
				}
			}

			avgTime = sum / numSuccess;

			log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.info("The test has completed!");
			log.info("Number of successful posts: " + numSuccess);
			log.info("Slowest response in milliseconds: " + maxTime);
			log.info("Fastest response in milliseconds: " + minTime);
			log.info("Average response in milliseconds: " + avgTime);
		} catch(ExecutionException e){
			log.error("Unable to get IDAMetric result from thread's Future object.", e);
		} catch(InterruptedException e){
			log.error("Unable to get IDAMetric result from thread's Future object.", e);
		}

	}
}

