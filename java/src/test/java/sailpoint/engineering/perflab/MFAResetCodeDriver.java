package sailpoint.engineering.perflab;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import sailpoint.services.idn.sdk.object.account.*;
import sailpoint.services.idn.sdk.services.AccountService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class MFAResetCodeDriver implements MFADriver {

    private static final String CC_RDS_MYSQL_URL = "dev02-useast1-cc.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com";
    private static final String CC_RDS_MYSQL_USERNAME = "admin20170151";

    private MFAType mfaType;
    private String ccPassword;

    MFAResetCodeDriver(MFAType mfaType, String ccPassword) {
        this.mfaType = mfaType;
        this.ccPassword = ccPassword;
    }

    @Override
    public MFAType getMFAType() {
        return mfaType;
    }

    @Override
    public String execute(AccountService accountService, String jptToken, String username)
            throws NullPointerException, IOException, IllegalStateException {

        //Request for password reset through password reset code
        JPTResult mfaSend = accountService.mfaSend(mfaType.name(), jptToken).execute().body();

        //Read the code from cc database for the code
        String query = "{\"db_user\":\"" + CC_RDS_MYSQL_USERNAME + "\",\"query\":\"select passwd_reset_key from user where alias = '" +
                username + "' and passwd_reset_key is not null\",\"host\":\"" + CC_RDS_MYSQL_URL + "\",\"db_pass\":\"" + ccPassword +
                "\",\"db\":\"cloudcommander\"}\n";
        String passwordResetCode;
        AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        InvokeRequest req = new InvokeRequest().withFunctionName("infra-db-client-dev-select").withPayload(query);
        InvokeResult requestResult = lambdaClient.invoke(req);
        ByteBuffer byteBuf = requestResult.getPayload();
        if (byteBuf != null) {
            String resetCode = StandardCharsets.UTF_8.decode(byteBuf).toString().replaceAll("[\\D]", "");
            if (resetCode.length() != 6) {
                throw new IllegalStateException("The password reset code from lambda is not in correct format for " + username + ". The code is " + resetCode);
            } else {
                passwordResetCode = resetCode;
            }
        } else {
            throw new IllegalStateException("Failed to retrieve password reset code from aws lambda for " + username + ".");
        }

        //Verify answer and return the JPT token
        MFAChallenge resetCodeChallenge = new MFAChallenge("code", passwordResetCode);
        JPTResult mfaVerifyResult = accountService.mfaVerify(mfaSend.JPT,
                new MFAVerify(mfaType.name(), Collections.singletonList(resetCodeChallenge))).execute().body();
        return mfaVerifyResult.JPT;
    }
}
