package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** 
 * A "textbook" (i.e. https://github.com/square/okhttp/wiki/Interceptors) 
 * implementation of a logging intercepter.  This one logs the http transaction
 * when DEBUG level logging is enabled on the LoggingInterceptor class. 
 * 
 * @author adam.hampton
 *
 */
public class LoggingInterceptor implements Interceptor {

	public final static Logger log = LogManager.getLogger(LoggingInterceptor.class);

	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		Request request = chain.request();

		long t1 = System.nanoTime();
		log.debug(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

		Response response = chain.proceed(request);

		long t2 = System.nanoTime();
		log.debug(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

		return response;
	}

}
