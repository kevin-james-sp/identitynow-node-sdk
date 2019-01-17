package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sailpoint.services.idn.sdk.object.accessrequest.RequestableObject;

import java.util.List;

public interface AccessRequestService {

    @GET("/beta/requestable-objects")
    Call<List<RequestableObject>> getRequestableObjects(@Query("limit") String limit, @Query("offset") String offset, @Query("identity-id") String identityId,
                                                        @Query("types") String types, @Query("sorters") String sorters);

}
