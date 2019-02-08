package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sailpoint.services.idn.sdk.object.role.Role;

public interface RoleService {

    @POST("/cc/api/role/create" )
    Call<Role> create (@Body Role role);

    @POST("/cc/api/role/update")
    Call<Role> update(@Body Role role);

    @POST("/cc/api/role/refresh")
    Call<Void> refresh();

}
