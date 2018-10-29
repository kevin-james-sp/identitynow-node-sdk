package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

public interface CampaignService {

	@GET( "cc/api/campaign/list" )
	Call<ResponseBody> campaignList (
			@Query("completedOnly") String completedOnlyBool,
			@Query("start") String start,
			@Query("limit") String limit);

	@POST("cc/api/campaign/create")
	@FormUrlEncoded
	Call<ResponseBody> campaignCreate(
			@FieldMap Map<String,String> params);

	@GET("cc/api/campaignFilter/list")
	Call<ResponseBody> campaignFilterList();
}
