package sailpoint.engineering.perflab;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.account.*;
import sailpoint.services.idn.sdk.services.AccountService;
import sailpoint.services.idn.session.SessionType;

import java.util.Collections;

public class Tets {

    public static void main(String[] args) throws Exception {

        IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
        ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);

        AccountService accountService = ids.getAccountService();

        JPTResult jptResult = accountService.pwdStart(new PasswordStart("1057", "perflab-05121458", "pswd-reset")).execute().body();

        PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();

        MFADetails mfaDetails = accountService.mfaDetails("KBA", passwordIsReady.JPT).execute().body();

        MFAChallenge kbaCityBornChallenge = mfaDetails.data.challenges.stream().filter(mfaChallenge -> mfaChallenge.text.equals("What city were you born in?")).findAny().orElse(null);

        kbaCityBornChallenge.answer = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"; //sha256 hash of "test" as the answer of the kba answer

        JPTResult mfaVerifyResult = accountService.mfaVerify(mfaDetails.JPT, new MFAVerify("KBA", Collections.singletonList(kbaCityBornChallenge))).execute().body();


//        ResponseBody a = mfaDetails.errorBody();
//        String b = a.string();

        //MFADetails mfaDetails = accountService.mfaDetails("KBA", jptResult.JPT).execute().body();


        System.out.println(1);
    }
}
