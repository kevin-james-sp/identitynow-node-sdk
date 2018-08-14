package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClusterService {

	//null okay for the Query query.
	@GET( "/cc/api/cluster/list" )
	Call<ResponseBody> clusterList (@Query("dc") long dc,
	                                        @Query("operational") boolean operational,
	                                        @Query("query") String query);

}
