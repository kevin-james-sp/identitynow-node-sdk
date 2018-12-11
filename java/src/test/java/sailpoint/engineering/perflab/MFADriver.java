package sailpoint.engineering.perflab;

import sailpoint.services.idn.sdk.services.AccountService;

import java.io.IOException;

public interface MFADriver {

    MFAType getMFAType();

    String execute(AccountService accountService, String jptToken, String username) throws NullPointerException, IOException, IllegalStateException;
}
