package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IdentityService {
	
	@FormUrlEncoded
	@POST( "/cc/api/user/invite" )
	Call<ResponseBody> invite ( @Field( "ids" ) String userId );
	
//	@POST( "/api/user/attestUsageCert" )
//	Call<ResponseBody> attestUsageCert (  );
//	
//	@POST( "/api/user/create" ) 
//	Call<ResponseBody> create (  );
//	
//	@POST( "/api/user/delete" ) 
//	Call<ResponseBody> delete (  );
//	
//	@GET( "/api/user/details" ) 
//	Call<ResponseBody> details ( );
//	
//	@POST( "/api/user/enabled" ) 
//	Call<ResponseBody> enabled ( );
//	
//	@GET( "/api/user/get" ) 
//	Call<ResponseBody> get ( );
//	
//	@GET( "/api/user/getExportReport" ) 
//	Call<ResponseBody> getExportReport ( );
//	
//	@GET( "/api/user/getCompletedExportReport" )
//	Call<ResponseBody> getCompletedExportReport ( );
//	
//	@POST( "/api/user/getPasswordResetDetails" ) 
//	Call<ResponseBody> getPasswordResetDetails ( );
//	
//	@GET( "/api/user/getStrongAuthMethods" ) 
//	Call<ResponseBody> getStrongAuthMethods ( );
//	
//	@POST( "/api/user/isPasswordValid" ) 
//	Call<ResponseBody> isPasswordValid ( );
//
	@GET( "/cc/api/user/list?_dc=1534949498371&query=&filters={\"joinOperator\":\"OR\",\"filter\":[{\"property\":\"name\",\"value\":\"\"},{\"property\":\"alias\",\"value\":\"\"},{\"property\":\"email\",\"value\":\"\"}]}&limit=100&page=1&start=0&sorters=[{\"property\":\"name\",\"direction\":\"ASC\"}]" )
	Call<ResponseBody> list ( );

	@GET("/cc/api/user/list")
	Call<ResponseBody> customList(@Query("_dc") String dc,
	                              @Query("query") String query,
	                              @Query("filters") String filters,
	                              @Query("limit") String limit,
	                              @Query("page") String page,
	                              @Query("start") String start,
	                              @Query("sorters") String sorters);
//	
//	@POST( "/api/user/preview" ) 
//	Call<ResponseBody> preview ( );
//	
//	@POST( "/api/user/register" ) 
//	Call<ResponseBody> register ( );
//	
//	@POST( "/api/user/reset" ) 
//	Call<ResponseBody> reset ( );
//	
//	@POST( "/api/user/resetPassword" ) 
//	Call<ResponseBody> resetPassword ( );
//	
//	@POST( "/api/user/resetSessionIdleTime" ) 
//	Call<ResponseBody> resetSessionIdleTime ( );
//	
//	@POST( "/api/user/runExportReport" ) 
//	Call<ResponseBody> runExportReport ( );
//	
//	@GET( "/api/user/roles" ) 
//	Call<ResponseBody> roles ( );
//	
//	@POST( "/api/user/sendUserName" ) 
//	Call<ResponseBody> sendUserName ( );
//	
//	@POST( "/api/user/sendVerificationToken" ) 
//	Call<ResponseBody> sendVerificationToken ( );
//	
//	@POST( "/api/user/setSessionAttributes" ) 
//	Call<ResponseBody> setSessionAttributes ( );
//	
//	@GET( "/api/user/showTerminateOption" ) 
//	Call<ResponseBody> showTerminateOption ( );
//	
//	@GET( "/api/user/status" )
//	Call<ResponseBody> status ( );
//	
//	@POST( "/api/user/strongAuthn" ) 
//	Call<ResponseBody> strongAuthn ( );
//	
//	@POST( "/api/user/synchronizeAttributes " ) 
//	Call<ResponseBody> synchronizeAttributes ( );
//	
//	@POST( "/api/user/terminate" ) 
//	Call<ResponseBody> terminate ( );
//	
//	@GET( "/api/user/testCISLinkModel" ) 
//	Call<ResponseBody> testCISLinkModel ( );
//	
//	@POST( "/api/user/update" ) 
//	Call<ResponseBody> update ( );
//	
//	@POST( "/api/user/updateLifecycleState" ) 
//	Call<ResponseBody> updateLifecycleState ( );
//	
//	@POST( "/api/user/updatePermissions" )
//	Call<ResponseBody> updatePermissions ( );
}
