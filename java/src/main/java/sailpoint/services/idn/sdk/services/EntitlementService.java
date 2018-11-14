package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sailpoint.services.idn.sdk.object.entitlement.EntitlementList;

public interface EntitlementService {

    @GET( "/cc/api/entitlement/list" )
    Call<EntitlementList> list (@Query("limit") int limit, @Query("CISApplicationId") String CISApplicationId);

}
