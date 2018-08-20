package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClusterService {


	//Cannot be null, or the query will be dropped from the url. The ui sets the query to an empty string.
	@GET( "/cc/api/cluster/list" )
	Call<ResponseBody> clusterList (@Query("dc") long dc);
}
