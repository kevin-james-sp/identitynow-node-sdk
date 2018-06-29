package sailpoint.services.idn.sdk.interceptor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
		return "FAILED TO CONVERT BODY TO STRING";
	}

	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		
		Request request = chain.request();
		
		// log.debug(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
		String logMsg;
		String headersStr = request.headers().toString();
		headersStr = headersStr.replaceAll("\\n", " ");
		if (null != request.body()) {
			String bodyStr = bodyToString(request.body());
			
			logMsg = String.format(">> %s %s headers:%s bytes:%d body:%s",
					request.method(),  request.url(), headersStr,
					request.body().contentLength(), bodyStr	
			); 
			
		} else {
			logMsg = String.format(">> %s %s headers:%s ",
					request.method(), request.url(), headersStr
			);
		}
		log.debug(logMsg);

		long t1 = System.nanoTime();
		Response response = chain.proceed(request);
		long t2 = System.nanoTime();
		
		String responseHeadsers = response.headers().toString();
		responseHeadsers = responseHeadsers.replaceAll("\\n", " ");
		
		String responseBody = "";
		if (response.body() != null) {
			responseBody = response.body().string();
		}
		
		logMsg = String.format("<< %s in %.1fms headers:%s body:%s", 
				response.request().url(), 
				(t2 - t1) / 1e6d, 
				responseHeadsers,
				responseBody
			);
		log.debug(logMsg);

		// Rebuild the 1-shot response body.
		ResponseBody body = ResponseBody.create(response.body().contentType(), responseBody);
		return response.newBuilder().body(body).build();

	}

}
