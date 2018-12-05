package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.*;
import sailpoint.services.idn.sdk.object.account.*;

public interface AccountService {

    @POST("/cc/password/start" )
    Call<JPTResult> pwdStart (@Body PasswordStart passwordStart);

    @GET("/cc/password/isReady" )
    Call<PasswordIsReady> pwdIsReady (@Header("Slpt-Jpt") String jptToken);

    @POST("/cc/password/reset")
    Call<JPTResult> pwdReset (@Header("Slpt-Jpt") String jptToken, @Body MFAVerify mfaVerify);

    @GET("/cc/mfa/details" )
    Call<MFADetails> mfaDetails (@Query("id") String id, @Header("Slpt-Jpt") String jptToken);

    @POST("/cc/mfa/verify")
    Call<JPTResult> mfaVerify (@Header("Slpt-Jpt") String jptToken, @Body MFAVerify mfaVerify);
}
