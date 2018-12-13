package sailpoint.services.idn.sdk.services;

import retrofit2.Call;
import retrofit2.http.*;
import sailpoint.services.idn.sdk.object.account.*;

public interface AccountService {

    @POST("/cc/password/start" )
    Call<JPTResult> pwdStart (@Body PasswordStart passwordStart);

    @GET("/cc/password/isReady" )
    Call<PasswordIsReady> pwdIsReady (@Header("Slpt-Jpt") String jptToken);

    @GET("/cc/password/getPswdPolicy" )
    Call<PasswordPolicy> getPasswordPolicy (@Header("Slpt-Jpt") String jptToken);

    @POST("/cc/password/reset")
    Call<JPTResult> pwdReset (@Header("Slpt-Jpt") String jptToken, @Body PasswordReset passwordReset);

    @GET("/cc/password/unlock" )
    Call<JPTResult> pwdUnlock (@Header("Slpt-Jpt") String jptToken);

    @GET("/cc/password/poll" )
    Call<PasswordPoll> pwdPoll (@Header("Slpt-Jpt") String jptToken);

    @GET("/cc/mfa/details" )
    Call<MFADetails> mfaDetails (@Query("id") String id, @Header("Slpt-Jpt") String jptToken);

    @POST("/cc/mfa/send" )
    Call<JPTResult> mfaSend (@Query("id") String id, @Header("Slpt-Jpt") String jptToken);

    @POST("/cc/mfa/verify")
    Call<JPTResult> mfaVerify (@Header("Slpt-Jpt") String jptToken, @Body MFAVerify mfaVerify);
}
