package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

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
	

	private static String bodyToString(final RequestBody request) {
		try {
			final RequestBody copy = request;
			final Buffer buffer = new Buffer();
			copy.writeTo(buffer);
			return buffer.readUtf8();
		} catch (final IOException e) {
			log.error("Failure converting RequestBody to string", e);
		}
		return null;
	}

	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		
		Request request = chain.request();

		long t1 = System.nanoTime();
		// log.debug(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
		
		if (null != request.body()) {
			log.debug(String.format("Sending request %s on %s%n%s body: ", request.url(), chain.connection(), request.headers()), bodyToString(request.body()));
		} else {
			log.debug(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
		}
		

		Response response = chain.proceed(request);

		long t2 = System.nanoTime();
		log.debug(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

		return response;
	}

}
