package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface MantisService {

	//no mantis calls will work, fml

	@Headers("Content-Type:application/json")
	@POST("/mantis/sources/schema/{sourceId}/addAttributeToGroupSchema")
	Call<ResponseBody> addAttributeToGroupSchema(@Path ("sourceId") String sourceId,
	                                             @Body Map schemaAttributeRequest);

	@POST("/mantis/sources/sources/{sourceId}")
	Call<ResponseBody> updateApplication(@Path ("sourceId") String sourceId,
	                                     @Body Map attributes);

	@POST("/mantis/sources/schema/{sourceId}/deleteAttributesFromGroupSchema")
	Call<ResponseBody> deleteAttributesFromGroupSchema(@Path("sourceId") String sourceId,
	                                                   @Field("schemaAttributeRequest") Map schemaAttributeRequest);

	@POST("/mantis/sources/schema/{sourceId}/deleteGroupSchema")
	Call<ResponseBody> deleteGroupSchema(@Path("sourceId") String sourceId);

	@GET("/mantis/sources/sources/{sourceId}/buildMapRule")
	Call<ResponseBody> getBuildMapRule(@Path("sourceId") String sourceId);

	@GET("/mantis/sources/sources/{sourceId}/JDBCProvisionRule")
	Call<ResponseBody> getJDBCProvisionRule(@Path("sourceId") String sourceId);

	@Headers("Content-Type:application/json")
	@POST("/mantis/sources/sources/{sourceId}/buildMapRule")
	Call<ResponseBody> postBuildMapRule(@Path("sourceId") String sourceId,
	                                    @Body String sourceCode);

	@Headers("Content-Type:application/json")
	@POST("/mantis/sources/sources/{sourceId}/JDBCProvisionRule")
	Call<ResponseBody> postJDBCProvisionRule(@Path("sourceId") String sourceId,
	                                         @Body String sourceCode);

	@DELETE("/mantis/sources/sources/{sourceId}/buildMapRule")
	Call<ResponseBody> deleteBuildMapRule(@Path("sourceId") String sourceId);

	@DELETE("/mantis/sources/sources/{sourceId}/JDBCProvisionRule")
	Call<ResponseBody> deleteJDBCProvisionRule(@Path("sourceId") String sourceId);

	@Headers("Content-Type:application/json")
	@POST("/mantis/provisioning/provisioningPolicies/{sourceId}/discoverCreatePolicy")
	Call<ResponseBody> discoverCreatePolicy(@Path("sourceId") String sourceId);

}
