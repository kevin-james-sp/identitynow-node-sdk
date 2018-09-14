package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface MantisService {

	//no mantis calls will work, fml
	@FormUrlEncoded
	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2FaddAttributeToGroupSchema")
	Call<ResponseBody> addAttributeToGroupSchema(@Field ("sourceId") String sourceId,
	                                             @Field("schemaAttributeRequest") String schemaAttributeRequest);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2FdeleteAttributesFromGroupSchema")
	Call<ResponseBody> deleteAttributesFromGroupSchema(@Field("sourceId") String sourceId,
	                                                   @Field("schemaAttributeRequest") Map schemaAttributeRequest);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2FdeleteGroupSchema")
	Call<ResponseBody> deleteGroupSchema(@Field("sourceId") String sourceId);

	@GET("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FbuildMapRule")
	Call<ResponseBody> getBuildMapRule(@Path("sourceId") String sourceId);

	@GET("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FJDBCProvisionRule")
	Call<ResponseBody> getJDBCProvisionRule(@Path("sourceId") String sourceId);

	@Headers("Content-Type:application/json")
	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FbuildMapRule")
	Call<ResponseBody> postBuildMapRule(@Path("sourceId") String sourceId,
	                                    @Field("sourceCode") String sourceCode);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FJDBCProvisionRule")
	Call<ResponseBody> postJDBCProvisionRule(@Path("sourceId") String sourceId,
	                                         @Field("sourceCode") String sourceCode);

	@DELETE("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FbuildMapRule")
	Call<ResponseBody> deleteBuildMapRule(@Path("sourceId") String sourceId);

	@DELETE("/debug/v2/mantis/post?path=sources%2Fsources%2F{sourceId}%2FJDBCProvisionRule")
	Call<ResponseBody> deleteJDBCProvisionRule(@Path("sourceId") String sourceId);

	@POST("/debug/v2/mantis/post?path=provisioning%2FprovisioningPolicies%2F{sourceId}%2FdiscoverCreatePolicy")
	Call<ResponseBody> discoverCreatePolicy(@Path("sourceId") String sourceId);


}
