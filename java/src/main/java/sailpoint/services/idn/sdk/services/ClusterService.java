package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Headers;

public interface ClusterService {


	//Cannot be null, or the query will be dropped from the url. The ui sets the query to an empty string.
	@Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:61.0) Gecko/20100101 Firefox/61.0")
	@GET( "/cc/api/cluster/list" )
	Call<ResponseBody> clusterList (@Query("dc") long dc);
}
