package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sailpoint.services.idn.sdk.object.accessprofile.AccessProfile;

public interface AccessProfileService {

	@POST
	Call<AccessProfile> accessProfile(@Body AccessProfile accessProfile);

}
