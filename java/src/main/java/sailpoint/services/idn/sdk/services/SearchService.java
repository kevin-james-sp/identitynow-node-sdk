package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.GET;
import sailpoint.services.idn.sdk.object.Identity;

import java.util.List;

public interface SearchService {

    @GET( "/cc/api/source/list" )
    Call<List<Identity>> list ();
}
