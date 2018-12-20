package sailpoint.services.idn.sdk.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A "textbook" (i.e. https://github.com/square/okhttp/wiki/Interceptors)
 * implementation of an interceptor. This one is intended to gather time data on a given request, and find the slowest
 * call in a test.
 *
 * @author alexander.strong
 *
 */
public class MetricsInterceptor implements Interceptor {

	//It is expected that this map will be used for a single test. Therefore it will contain all times for every type of
	//call in the test.
	HashMap<String, LinkedList<Long>> globalCallTimes = new HashMap<>();

	public final static Logger log = LogManager.getLogger(MetricsInterceptor.class);


	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		Request request = chain.request();
		Response response = chain.proceed(request);
		long t1 = response.sentRequestAtMillis();
		long t2 = response.receivedResponseAtMillis();

		//if there is already a list of times, add this call time to that list, else create a new list and add it.
		//TODO: Probably url is null
		LinkedList<Long> specificCallTimes = globalCallTimes.get(request.url().toString());
		if(specificCallTimes == null){
			specificCallTimes = new LinkedList<Long>();
		}

		specificCallTimes.push(t2 - t1);
		globalCallTimes.put(request.url().toString(), specificCallTimes);
		return response;
	}

	public HashMap<String, LinkedList<Long>> getGlobalCallTimes(){
		return globalCallTimes;
	}

	public void purgeTimes(){
		globalCallTimes = new HashMap<String, LinkedList<Long>>();
	}

	/**
	 * This is a helper method to calculate the slowest call in the map of all calls.
	 * @return The url of the slowest call. Slowest call is measured by comparing average call times of each call type
	 * @throws ClassCastException if a given call time cannot be mapped to a Double or OptionalDouble
	 * @throws NullPointerException if the method is unable to find a slowest call.
	 */
	public String getSlowestCall() throws ClassCastException, NullPointerException{
		Double slowest = new Double(-1);
		String slowestCall = null;
		for(String key : globalCallTimes.keySet()){
			LinkedList<Long> callTimes = globalCallTimes.get(key);
			Double average = callTimes.stream().mapToDouble(val -> val).average().orElseThrow(ClassCastException::new);
			if(average.doubleValue() > slowest){
				slowest = average;
				slowestCall = key;
			}
		}

		if(slowestCall == null){
			throw new NullPointerException("Unable to find the slowest call");
		}
		return slowestCall;
	}
}
