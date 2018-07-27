package sailpoint.services.idn.sdk.services;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SourceService {
	
	/*
	 * Basics
	 */
	
	@GET( "/api/source/list" )
	Call<ResponseBody> list (  );
	
	@GET( "/api/source/get" ) 
	Call<ResponseBody> get (  );
	
	@POST( "/api/source/create" )
	Call<ResponseBody> create (  );
	
	@POST( "/api/source/update" )
		Call<ResponseBody> update (  );
	
	@POST( "/api/source/delete" )
	Call<ResponseBody> delete (  );
	
	@GET( "/api/source/export" )
	Call<ResponseBody> exportSource (  );
	
	@POST( "/api/source/import" )
	Call<ResponseBody> importSource (  );
	
	/*
	 * Schema
	 */
	
	@GET( "/api/source/getAccountSchema" )
	Call<ResponseBody> getAccountSchema (  );
	
	@POST( "/api/source/discoverSchema" )
	Call<ResponseBody> discoverSchema (  );
	
	@POST( "/api/source/createSchemaAttribute" )
	Call<ResponseBody> createSchemaAttribute (  );
	
	@POST( "/api/source/updateSchemaAttributes" )
	Call<ResponseBody> updateSchemaAttributes (  );
	
	@POST( "/api/source/deleteSchemaAttribute" )
	Call<ResponseBody> deleteSchemaAttribute (  );
	
	/*
	 * Aggregation
	 */
	
	@GET( "/api/source/supportsAggregation" )
	Call<ResponseBody> supportsAggregation ( );
	
	@GET( "/api/source/getAggregationSchedules" )
	Call<ResponseBody> getAggregationSchedules ( );
	
	@POST( "/api/source/testConnection/{id}" )
	Call<ResponseBody> testConnection(
			@Path( "id" ) String id );
	
	@POST( "/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id );
	
	@Multipart
	@POST( "/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id,
			@Part( "file\"; filename=\"file.csv\" " ) RequestBody file );
	
	@POST( "/api/source/cancelAggregation" )
	Call<ResponseBody> cancelAggregation (  );
	
	@POST( "/api/source/loadEntitlements/{id}" )
	Call<ResponseBody> aggregateEntitlements ( 
			@Path( "id" ) String id );
	
	@POST( "/api/source/scheduleAggregation" )
	Call<ResponseBody> scheduleAggregation (  );
	
	@POST( "/api/source/scheduleEntitlementAggregation" )
	Call<ResponseBody> scheduleEntitlementAggregation (  );
	
	/*
	 * Correlation
	 */
	
	@GET( "/api/source/getUncorrelatedAccounts" )
	Call<ResponseBody> getUncorrelatedAccounts (  );
	
	@GET( "/api/source/getUncorrelatedAccountsCount" )
	Call<ResponseBody> getUncorrelatedAccountsCount (  );
	
	/*
	 * Uploads
	 */
	
	@POST( "/api/source/uploadConnectorFile" )
	Call<ResponseBody> uploadConnectorFile (  );
	
	@POST( "/api/source/uploadCustomIcon" )
	Call<ResponseBody> uploadCustomIcon (  );
	
	@POST( "/api/source/importEntitlementsCsv" )
	Call<ResponseBody> importEntitlementsCsv (  );
	
	/*
	 * Reporting
	 */
	
	@FormUrlEncoded
	@POST( "/api/source/runAccountsExportReport" )
	Call<ResponseBody> runAccountsExportReport(
		@Field( "sourceId" ) String sourceId,
		@Field( "reportName" ) String reportName );
		  
	@GET( "/api/source/getAccountsExportReport" )
	Call<ResponseBody> getAccountsExportReport(
		@Query( "sourceId" ) String sourceId,
		@Query( "reportName" ) String reportName );
	
	@GET( "/api/source/exportAccountFeed" )
	Call<ResponseBody> exportAccountFeed( );
	
	@GET( "/api/source/exportEntitlementsCsv" )
	Call<ResponseBody> exportEntitlementsCsv( );
	
	@GET( "/api/source/getAccountsCsv" )
	Call<ResponseBody> getAccountsCsv( );
	
	@POST( "/api/source/runAccountsExportReport" )
	Call<ResponseBody> runAccountsExportReport( );
	
	@POST( "/api/source/runProvisioningSummary" ) 
	Call<ResponseBody> runProvisioningSummary( );
	
	@POST( "/api/source/runResetSummary" )
	Call<ResponseBody> runResetSummary( );
	
	/*
	 * Misc
	 */
	
	@GET( "/api/source/connections" )
	Call<ResponseBody> connections (  );
	
	@POST( "/api/source/createNotification" )
	Call<ResponseBody> createNotification (  );
	
	@GET( "/api/source/getDeleteThreshold" )
	Call<ResponseBody> getDeleteThreshold (  );
	
	@POST( "/api/source/updateDeleteThreshold" )
	Call<ResponseBody> updateDeleteThreshold (  );
	
	@GET( "/api/source/getAccountsTemplate" )
	Call<ResponseBody> getAccountsTemplate (  );
	
	@GET( "/api/source/getProvisioningSummary" )
	Call<ResponseBody> getProvisioningSummary (  );
	
	@GET( "/api/source/getApplicationConfig" )
	Call<ResponseBody> getApplicationConfig (  );
	
	@GET( "/api/source/getAttributeSyncConfig" )
	Call<ResponseBody> getAttributeSyncConfig (  );
	
	@GET( "/api/source/getConfig" )
	Call<ResponseBody> getConfig (  );
	
	@GET( "/api/source/getEntitlementAggregationSchedules" )
	Call<ResponseBody> getEntitlementAggregationSchedules (  );
	
	@GET( "/api/source/getEntitlementsTemplate" )
	Call<ResponseBody> getEntitlementsTemplate (  );
	
	@GET( "/api/source/getPasswordSyncInfo" )
	Call<ResponseBody> getPasswordSyncInfo (  );
	
	@GET( "/api/source/getResetSummary" )
	Call<ResponseBody> getResetSummary (  );
	
	@GET( "/api/source/getSourceDefinitions" )
	Call<ResponseBody> getSourceDefinitions (  );
	
	@POST( "/api/source/loadUncorrelatedAccounts" )
	Call<ResponseBody> loadUncorrelatedAccounts (  );
	
	@POST( "/api/source/reset" )
	Call<ResponseBody> reset (  );
	
	@POST( "/api/source/resetAll" )
	Call<ResponseBody> resetAll (  );
	
	@POST( "/api/source/setAttributeSyncConfig" )
	Call<ResponseBody> setAttributeSyncConfig (  );
	
	@POST( "/api/source/setPassword" )
	Call<ResponseBody> setPassword (  );
	
	@GET( "/api/source/status" )
	Call<ResponseBody> status (  );
	
	@POST( "/api/source/syncPassword" )
	Call<ResponseBody> syncPassword (  );
	
}
