package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sailpoint.services.idn.sdk.object.IAI.Oauth.AccessToken;
import sailpoint.services.idn.sdk.object.IAI.recommender.RecommenderFields;
import sailpoint.services.idn.sdk.object.IAI.recommender.ResponseElement;

public interface IAIService {

	@POST( "beta/recommendations/request" )
	Call<ResponseElement> recommendationRequest (@Header ("Authorization") String authorization,
	                                             @Header("Content-Type") String contentType,
	                                             @Body RecommenderFields recommenderFields);

	@POST("oauth/token?grant_type=client_credentials")
	Call<AccessToken> refreshToken(@Header("Authorization") String authorization);
}
