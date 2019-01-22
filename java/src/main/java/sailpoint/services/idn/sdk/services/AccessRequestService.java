package sailpoint.services.idn.sdk.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRequest;
import sailpoint.services.idn.sdk.object.accessrequest.AccessRevoke;
import sailpoint.services.idn.sdk.object.accessrequest.RequestableObject;

import java.util.List;
import java.util.Map;

public interface AccessRequestService {

    @GET("/beta/requestable-objects")
    Call<List<RequestableObject>> getRequestableObjects(@Query("limit") String limit, @Query("offset") String offset, @Query("identity-id") String identityId,
                                                        @Query("types") String types, @Query("sorters") String sorters);

    @GET("/beta/requestable-objects/{requestableObjectId}/requestable-for-identities")
    Call<List<RequestableObject>> getRequestableIdentities(@Path("requestableObjectId") String requestableObjectId, @Query("limit") String limit, @Query("offset") String offset,
                                                           @Query("sorters") String sorters, @Query("filters") String filters);

    @POST("/beta/access-requests")
    Call<ResponseBody> accessRequest(@Body AccessRequest accessRequest);

    @POST("/cc/api/role/revoke")
    Call<Map<String, Object>> accessRevoke(@Body AccessRevoke accessRevoke);

}
