package sailpoint.engineering.perflab;

import sailpoint.services.idn.sdk.object.account.*;
import sailpoint.services.idn.sdk.services.AccountService;
import sailpoint.services.idn.util.PasswordUtil;

import java.io.IOException;
import java.util.Collections;

public class MFAKbaDriver implements MFADriver {

    private static final String PERF_KBA_ANSWER = "test";

    private MFAType mfaType = MFAType.KBA;

    public MFAKbaDriver() {}

    @Override
    public MFAType getMFAType() {
        return mfaType;
    }

    @Override
    public String execute(AccountService accountService, String jptToken, String username)
            throws NullPointerException, IOException, IllegalStateException {

        //Request for password reset through KBA question
        MFADetails mfaDetails = accountService.mfaDetails(mfaType.name(), jptToken).execute().body();
        MFAChallenge kbaCityBornChallenge = mfaDetails.data.challenges.stream().filter(mfaChallenge -> mfaChallenge.text.equals("What city were you born in?")).findAny().orElse(null);
        kbaCityBornChallenge.answer = PasswordUtil.encodeSha256String(PERF_KBA_ANSWER);

        //Verify answer and return the JPT token
        JPTResult mfaVerifyResult = accountService.mfaVerify(mfaDetails.JPT, new MFAVerify(mfaType.name(), Collections.singletonList(kbaCityBornChallenge))).execute().body();
        return mfaVerifyResult.JPT;
    }
}
