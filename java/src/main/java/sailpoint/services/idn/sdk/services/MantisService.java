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
	Call<ResponseBody> addAttributeToGroupSchema(@Field ("externalId") String externalId,
	                                             @Field("schemaAttributeRequest") String schemaAttributeRequest);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2FdeleteAttributesFromGroupSchema")
	Call<ResponseBody> deleteAttributesFromGroupSchema(@Field("externalId") String externalId,
	                                                   @Field("schemaAttributeRequest") Map schemaAttributeRequest);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2FdeleteGroupSchema")
	Call<ResponseBody> deleteGroupSchema(@Field("externalId") String externalId);

	@GET("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FbuildMapRule")
	Call<ResponseBody> getBuildMapRule(@Path("externalId") String externalId);

	@GET("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FJDBCProvisionRule")
	Call<ResponseBody> getJDBCProvisionRule(@Path("externalId") String externalId);

	@Headers("Content-Type:application/json")
	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FbuildMapRule")
	Call<ResponseBody> postBuildMapRule(@Path("externalId") String externalId,
	                                    @Field("sourceCode") String sourceCode);

	@POST("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FJDBCProvisionRule")
	Call<ResponseBody> postJDBCProvisionRule(@Path("externalId") String externalId,
	                                         @Field("sourceCode") String sourceCode);

	@DELETE("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FbuildMapRule")
	Call<ResponseBody> deleteBuildMapRule(@Path("externalId") String externalId);

	@DELETE("/debug/v2/mantis/post?path=sources%2Fsources%2F{externalId}%2FJDBCProvisionRule")
	Call<ResponseBody> deleteJDBCProvisionRule(@Path("externalId") String externalId);

	@POST("/debug/v2/mantis/post?path=provisioning%2FprovisioningPolicies%2F{externalId}%2FdiscoverCreatePolicy")
	Call<ResponseBody> discoverCreatePolicy(@Path("externalId") String externalId);

}
