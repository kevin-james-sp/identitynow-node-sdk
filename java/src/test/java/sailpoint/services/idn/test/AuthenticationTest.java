package sailpoint.services.idn.test;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.Tenant;


public final class AuthenticationTest {

	private static Map<String,Object> config;

	private static final Gson gson = new Gson();

	public static void main(String... args) throws Exception {

		config = gson.fromJson( FileUtils.readFileToString( new File( "src/test/resources/config.json" ) ), Map.class );

		Tenant tenant = new sailpoint.services.idn.sdk.object.Tenant( (Map<String,String>) config.get( "tenant" ) );

		IdentityNowService idnService = new IdentityNowService( tenant );
		
		System.out.println( gson.toJson( idnService.createSession() ) );
		
	}

}