package sailpoint.services.idn.sdk.services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

//TODO: Flesh out all calls. A warning to anyone using this library, not all calls are complete and may not function
//as intended or at all
//These calls do not explicitly state the need for an auth header, but they will need an auth header with an api token.
//We recommend the use of an interceptor to add the token to all calls when using this api.
public interface SourceService {
	
	/*
	 * Basics
	 */


	@GET( "/cc/api/source/list" )
	Call<ResponseBody> list (  );
	
	@GET( "/cc/api/source/get" )
	Call<ResponseBody> get (  );


	@POST( "/cc/api/source/create" )
	@FormUrlEncoded
	Call<ResponseBody> create (@FieldMap Map<String, String> params);


	@POST( "/cc/api/source/update/{sourceId}" )
	@FormUrlEncoded
	Call<ResponseBody> update (@FieldMap Map<String, String> params,
	                           @Path("sourceId") String sourceId);


//	@POST( "/cc/api/source/delete" )
//	Call<ResponseBody> delete (  );
//
//	@GET( "/cc/api/source/export" )
//	Call<ResponseBody> exportSource (  );
//
//	@POST( "/cc/api/source/import" )
//	Call<ResponseBody> importSource (  );
	
	/*
	 * Schema
	 */


//	@GET( "/cc/api/source/getAccountSchema" )
//	Call<ResponseBody> getAccountSchema (  );

	@POST( "/cc/api/source/discoverSchema/{sourceId}" )
	Call<ResponseBody> discoverSchema ( @Path("sourceId") String sourceId );
//
//	@POST( "/cc/api/source/createSchemaAttribute" )
//	Call<ResponseBody> createSchemaAttribute (  );
//
//	@POST( "/cc/api/source/updateSchemaAttributes/{sourceId}" )
//	@FormUrlEncoded
//	Call<ResponseBody> updateSchemaAttributes (@Path("sourceId") String sourceId,
//	                                           @FieldMap Map<String, String> params);
//
//	@POST( "/cc/api/source/deleteSchemaAttribute" )
//	Call<ResponseBody> deleteSchemaAttribute (  );

	/*
	 * Aggregation
	 */
	
//	@GET( "/cc/api/source/supportsAggregation" )
//	Call<ResponseBody> supportsAggregation ( );
//
//	@GET( "/cc/api/source/getAggregationSchedules" )
//	Call<ResponseBody> getAggregationSchedules ( );
	
	@POST( "/cc/api/source/testConnection/{id}" )
	Call<ResponseBody> testConnection(
			@Path( "id" ) String id );
	
	@POST( "/cc/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id );
	
	@Multipart
	@POST( "/cc/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id,
			@Part( "file\"; filename=\"file.csv\" " ) RequestBody file );
	
//	@POST( "/cc/api/source/cancelAggregation" )
//	Call<ResponseBody> cancelAggregation (  );
	
	@POST( "/cc/api/source/loadEntitlements/{id}" )
	Call<ResponseBody> aggregateEntitlements ( 
			@Path( "id" ) String id );
	
//	@POST( "/cc/api/source/scheduleAggregation" )
//	Call<ResponseBody> scheduleAggregation (  );
//
//	@POST( "/cc/api/source/scheduleEntitlementAggregation" )
//	Call<ResponseBody> scheduleEntitlementAggregation (  );
	
	/*
	 * Correlation
	 */
	
//	@GET( "/cc/api/source/getUncorrelatedAccounts" )
//	Call<ResponseBody> getUncorrelatedAccounts (  );
//
//	@GET( "/cc/api/source/getUncorrelatedAccountsCount" )
//	Call<ResponseBody> getUncorrelatedAccountsCount (  );
//
	/*
	 * Uploads
	 */
	
	@POST( "/cc/api/source/uploadConnectorFile/{sourceId}" )
	@Multipart
	Call<ResponseBody> uploadConnectorFile (@Path("sourceId") String sourceId,
	                                        @Part MultipartBody.Part filePart);
	
//	@POST( "/cc/api/source/uploadCustomIcon" )
//	Call<ResponseBody> uploadCustomIcon (  );
//
//	@POST( "/cc/api/source/importEntitlementsCsv" )
//	Call<ResponseBody> importEntitlementsCsv (  );
	
	/*
	 * Reporting
	 */

	@FormUrlEncoded
	@POST( "/cc/api/source/runAccountsExportReport" )
	Call<ResponseBody> runAccountsExportReport(
		@Field( "sourceId" ) String sourceId,
		@Field( "reportName" ) String reportName );
		  
	@GET( "/cc/api/source/getAccountsExportReport" )
	Call<ResponseBody> getAccountsExportReport(
		@Query( "sourceId" ) String sourceId,
		@Query( "reportName" ) String reportName );
	
//	@GET( "/cc/api/source/exportAccountFeed" )
//	Call<ResponseBody> exportAccountFeed( );
//
//	@GET( "/cc/api/source/exportEntitlementsCsv" )
//	Call<ResponseBody> exportEntitlementsCsv( );
//
//	@GET( "/cc/api/source/getAccountsCsv" )
//	Call<ResponseBody> getAccountsCsv( );
//
//	@POST( "/cc/api/source/runAccountsExportReport" )
//	Call<ResponseBody> runAccountsExportReport( );
//
//	@POST( "/cc/api/source/runProvisioningSummary" )
//	Call<ResponseBody> runProvisioningSummary( );
//
//	@POST( "/cc/api/source/runResetSummary" )
//	Call<ResponseBody> runResetSummary( );
	
	/*
	 * Misc
	 */
	
//	@GET( "/cc/api/source/connections" )
//	Call<ResponseBody> connections (  );
//
//	@POST( "/cc/api/source/createNotification" )
//	Call<ResponseBody> createNotification (  );
//
//	@GET( "/cc/api/source/getDeleteThreshold" )
//	Call<ResponseBody> getDeleteThreshold (  );
//
//	@POST( "/cc/api/source/updateDeleteThreshold" )
//	Call<ResponseBody> updateDeleteThreshold (  );
//
//	@GET( "/cc/api/source/getAccountsTemplate" )
//	Call<ResponseBody> getAccountsTemplate (  );
//
//	@GET( "/cc/api/source/getProvisioningSummary" )
//	Call<ResponseBody> getProvisioningSummary (  );
//
//	@GET( "/cc/api/source/getApplicationConfig" )
//	Call<ResponseBody> getApplicationConfig (  );
//
//	@GET( "/cc/api/source/getAttributeSyncConfig" )
//	Call<ResponseBody> getAttributeSyncConfig (  );
//
//	@GET( "/cc/api/source/getConfig" )
//	Call<ResponseBody> getConfig (  );
//
//	@GET( "/cc/api/source/getEntitlementAggregationSchedules" )
//	Call<ResponseBody> getEntitlementAggregationSchedules (  );
//
//	@GET( "/cc/api/source/getEntitlementsTemplate" )
//	Call<ResponseBody> getEntitlementsTemplate (  );
//
//	@GET( "/cc/api/source/getPasswordSyncInfo" )
//	Call<ResponseBody> getPasswordSyncInfo (  );
//
//	@GET( "/cc/api/source/getResetSummary" )
//	Call<ResponseBody> getResetSummary (  );
//
//	@GET( "/cc/api/source/getSourceDefinitions" )
//	Call<ResponseBody> getSourceDefinitions (  );
//
//	@POST( "/cc/api/source/loadUncorrelatedAccounts" )
//	Call<ResponseBody> loadUncorrelatedAccounts (  );
//
//	@POST( "/cc/api/source/reset" )
//	Call<ResponseBody> reset (  );
//
//	@POST( "/cc/api/source/resetAll" )
//	Call<ResponseBody> resetAll (  );
//
//	@POST( "/cc/api/source/setAttributeSyncConfig" )
//	Call<ResponseBody> setAttributeSyncConfig (  );
//
//	@POST( "/cc/api/source/setPassword" )
//	Call<ResponseBody> setPassword (  );
//
//	@GET( "/cc/api/source/status" )
//	Call<ResponseBody> status (  );
//
//	@POST( "/cc/api/source/syncPassword" )
//	Call<ResponseBody> syncPassword (  );

}
