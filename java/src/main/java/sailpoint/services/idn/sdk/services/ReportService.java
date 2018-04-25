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
import sailpoint.services.idn.sdk.object.ReportResult;
import sailpoint.services.idn.sdk.object.TaskResult;

public interface ReportService {
		  
	@FormUrlEncoded
	@POST( "/api/source/runAccountsExportReport" )
	Call<TaskResult> runAccountsExportReport(
		@Field( "sourceId" ) String sourceId,
		@Field( "reportName" ) String reportName );
		  
	@GET( "/api/source/getAccountsExportReport" )
	Call<ReportResult> getAccountsExportReportStatus(
		@Query( "sourceId" ) String sourceId,
		@Query( "reportName" ) String reportName );
	
	@GET( "/api/report/get/{id}" )
	Call<ResponseBody> getReport(
			@Path( "id" ) String id, 
			@Query( "format" ) String format,
			@Query( "name" ) String name );

	@POST( "/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id );
	
	@Multipart
	@POST( "/api/source/loadAccounts/{id}")
	Call<ResponseBody> aggregateAccounts(
			@Path( "id" ) String id,
			@Part( "file\"; filename=\"file.csv\" " ) RequestBody file );
	
}
