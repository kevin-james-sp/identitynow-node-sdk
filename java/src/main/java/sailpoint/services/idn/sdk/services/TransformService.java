package sailpoint.services.idn.sdk.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import sailpoint.services.idn.sdk.object.Transform;

public interface TransformService {

	@GET( "/api/transform/list" )
	Call<List<Transform>> list();

	@GET( "/api/transform/{id}" )
	Call<Transform> get(
			@Path( "id" ) String id );
	
	@POST( "/api/transform/create" )
	Call<Transform> create ( 
			@Body Transform transform );
	
	@POST( "/api/transform/update" )
	Call<Transform> update ( 
			@Body Transform transform );
	
	@POST( "/api/transform/delete/{id}" )
	Call<Transform> delete ( 
			@Path( "id" ) String id );

}
