package sailpoint.services.idn.sdk.object;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Tenant {

	@SerializedName("alias")
	public String alias;

	@SerializedName("url")
	public final String url;

	@SerializedName("username")
	public final String username;

	@SerializedName("password")
	public final String password;

	@SerializedName("apiUser")
	public final String apiUser;

	@SerializedName("apiKey")
	public final String apiKey;

	public Tenant ( Map<String, String> map ) {
		this.alias = map.get( "alias" );
		this.url = map.get( "url" );
		this.username = map.get( "username" );
		this.password = map.get( "password" );
		this.apiUser = map.get( "apiUser" );
		this.apiKey = map.get( "apiKey" );
	}
	
	public Tenant ( String url, String username, String password, String apiUser, String apiKey ) {
		this.alias = null;
		this.url = url;
		this.username = username;
		this.password = password;
		this.apiUser = apiUser;
		this.apiKey = apiKey;
	}

	public Tenant ( String alias, String url, String username, String password, String apiUser, String apiKey ) {
		this.alias = alias;
		this.url = url;
		this.username = username;
		this.password = password;
		this.apiUser = apiUser;
		this.apiKey = apiKey;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getApiUser() {
		return apiUser;
	}

	public String getApiKey() {
		return apiKey;
	}
	  
}
