package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClientService {
	
	@FormUrlEncoded
	@POST( "/api/client/list" )
	Call<ResponseBody> invite ( @Field( "ids" ) String userId );

}
