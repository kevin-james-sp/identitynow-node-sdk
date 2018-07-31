package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClusterService {

	//null okay for the Query query.
	@GET( "/api/source/supportsAggregation" )
	Call<ResponseBody> supportsAggregation (@Query("dc") int dc,
	                                        @Query("operational") boolean operational,
	                                        @Query("query") String query);

}
