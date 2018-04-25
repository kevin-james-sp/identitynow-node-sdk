package sailpoint.services.idn.sdk.services;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ConnectorService {

	@GET( "/api/connector/list" )
	Call<ResponseBody> listConnectors();
	
	@FormUrlEncoded
	@Headers( "X-CSRF-Token: nocheck" )
	@POST( "/api/connector/create" )
	Call<ResponseBody> createConnector(
			@Field( "name" ) String name,
			@Field( "description" ) String description,
			@Field( "className" ) String className, 
			@Field( "directConnect" ) boolean directConnect,
			@Field( "status" ) String status );
	
	@Headers( "X-CSRF-Token: nocheck" )
	@POST( "/api/connector/delete/{id}" )
	Call<ResponseBody> deleteConnector(
			@Path( "id" ) String id );
	
	@GET( "/api/connector/export/{id}" )
	Call<ResponseBody> exportConnectorConfig(
			@Path( "id" ) String id );
	
	@Multipart
	@Headers( "X-CSRF-Token: nocheck" )
	@POST( "/api/connector/import/{id}" )
	Call<ResponseBody> importConnectorConfig( 
			@Path( "id" ) String id,
			@Part( "file\"; filename=\"file.zip\" " ) RequestBody file );
	
}
