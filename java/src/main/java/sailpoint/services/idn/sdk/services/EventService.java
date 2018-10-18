package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface EventService {

	//TODO: Make an event service if/when we start making other event calls
	@GET("/cc/api/event/list?")
	Call<ResponseBody> eventList(
			@QueryMap Map<String, String> parameters);
}
