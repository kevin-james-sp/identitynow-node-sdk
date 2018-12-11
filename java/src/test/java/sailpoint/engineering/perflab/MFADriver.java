package sailpoint.engineering.perflab;

import sailpoint.services.idn.sdk.services.AccountService;

import java.io.IOException;

public interface MFADriver {

    /**
     * Return the MFA type that the MFA driver is running on
     * @return the MFA type
     */
    MFAType getMFAType();

    /**
     * Execute the MFA validation for the MFA driver
     * @param accountService the service that communicate to CC endpoints
     * @param jptToken the JPT token for authentication purposes
     * @param username the username who's password is being reset
     * @return the JPT token after the MFA validation
     * @throws NullPointerException if in any step, the result failed and JPT token is not available
     * @throws IOException if account service failed to talk to CC end point
     * @throws IllegalStateException if in any step, the result is not valid
     */
    String execute(AccountService accountService, String jptToken, String username) throws NullPointerException, IOException, IllegalStateException;
}
