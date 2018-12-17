package sailpoint.services.idn.sdk.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class MetricsInterceptor implements Interceptor {

	HashMap<String, LinkedList<Long>> globalCallTimes;

	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		Request request = chain.request();
		Response response = chain.proceed(request);
		long t1 = response.sentRequestAtMillis();
		long t2 = response.receivedResponseAtMillis();
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
}
