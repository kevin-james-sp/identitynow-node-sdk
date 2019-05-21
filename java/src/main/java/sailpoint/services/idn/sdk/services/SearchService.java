package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sailpoint.services.idn.sdk.object.Identity;

import java.util.List;

public interface SearchService {

    @GET( "/v2/search/identities" )
    Call<List<Identity>> searchIdentities (@Query("limit") int limit, @Query("offset") int offset, @Query("query") String query);
}
