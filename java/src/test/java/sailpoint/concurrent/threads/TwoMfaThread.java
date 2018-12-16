package sailpoint.concurrent.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.engineering.perflab.MFADriver;
import sailpoint.engineering.perflab.MFAKbaDriver;
import sailpoint.engineering.perflab.MFAResetCodeDriver;
import sailpoint.engineering.perflab.MFAType;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.account.JPTResult;
import sailpoint.services.idn.sdk.object.account.Org;
import sailpoint.services.idn.sdk.object.account.PasswordIsReady;
import sailpoint.services.idn.sdk.object.account.PasswordPolicy;
import sailpoint.services.idn.sdk.object.account.PasswordPoll;
import sailpoint.services.idn.sdk.object.account.PasswordReset;
import sailpoint.services.idn.sdk.object.account.PasswordStart;
import sailpoint.services.idn.sdk.services.AccountService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.util.PasswordUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TwoMfaThread implements Callable<Boolean> {

	private String GOAL;
	private String username;
	private String MFA_KBA;
	private String MFA_EMAIL;
	private String MFA_SMS;
	private String CC_DB_PASSWORD;
	private String PERF_DEFAULT_PWD;

	private final static Logger log = LogManager.getLogger(TwoMfaThread.class);

	public TwoMfaThread(String GOAL, String username, String MFA_KBA, String MFA_EMAIL, String MFA_SMS, String CC_DB_PASSWORD, String PERF_DEFAULT_PWD){
		this.GOAL = GOAL;
		this.username = username;
		this.MFA_KBA = MFA_KBA;
		this.MFA_EMAIL = MFA_EMAIL;
		this.MFA_SMS = MFA_SMS;
		this.CC_DB_PASSWORD = CC_DB_PASSWORD;
		this.PERF_DEFAULT_PWD = PERF_DEFAULT_PWD;
	}

	/**
	 * Code to reset password or unlock account using "n" MFA.
	 * It loops through the available MFA drivers and randomly execute for the password reset or unlock account.
	 * @return true if the reset or unlock is successful. false otherwise
	 */
	@Override
	public Boolean call() throws Exception {
		String goalName = GOAL.equals("pswd-reset") ? "reset password" : "unlocked account";

		try {
			//Success message. Might be interrupted by any error message.
			String successMsg = "Successfully " + goalName + " for user " + username + " with ";

			//Initiate list of available drivers
			List<MFADriver> driverList = new ArrayList<MFADriver>() {{
				if (MFA_KBA.equals("true")) add(new MFAKbaDriver());
				if (MFA_EMAIL.equals("true")) {
					add(new MFAResetCodeDriver(MFAType.EMAIL_WORK, CC_DB_PASSWORD));
					add(new MFAResetCodeDriver(MFAType.EMAIL_PERSONAL, CC_DB_PASSWORD));
				}
				if (MFA_SMS.equals("true")) {
					add(new MFAResetCodeDriver(MFAType.SMS_WORK, CC_DB_PASSWORD));
					add(new MFAResetCodeDriver(MFAType.SMS_PERSONAL, CC_DB_PASSWORD));
				}
			}};

			//Create account service without session or access token
			ClientCredentials clientCredentials = EnvironmentCredentialer.getEnvironmentCredentials();
			IdentityNowService ids = new IdentityNowService(clientCredentials);
			ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC);
			AccountService accountService = ids.getAccountService();

			//Request for password reset or unlock account
			JPTResult jptResult = accountService.pwdStart(new PasswordStart(username, clientCredentials.getOrgName(), GOAL)).execute().body();
			PasswordIsReady passwordIsReady = accountService.pwdIsReady(jptResult.JPT).execute().body();

			//Keep doing MFA until the driver list is empty or the password ready signal is returned by CC
			while (!passwordIsReady.ready && driverList.size() > 0) {
				MFADriver driver = driverList.remove((int)(Math.random() * driverList.size()));
				String jptToken = driver.execute(accountService, passwordIsReady.JPT, username);
				passwordIsReady = accountService.pwdIsReady(jptToken).execute().body();

				successMsg += driver.getMFAType().getFriendlyName() + " and ";
			}

			//Replace the last "and" for readability
			successMsg = successMsg.replaceAll(" and $", ".");

			String pollingJPTToken;
			if (GOAL.equals("pswd-reset")) {
				//Getting password policy and send reset password request
				PasswordPolicy passwordPolicy = accountService.getPasswordPolicy(passwordIsReady.JPT).execute().body();
				Org orgKeyInfo = passwordPolicy.org;
				JPTResult passwordChangeResult = accountService.pwdReset(passwordIsReady.JPT, new PasswordReset(username,
						PasswordUtil.encodeSha256String(PERF_DEFAULT_PWD),
						PasswordUtil.getPassthroughHash(username, orgKeyInfo.encryptionKey),
						PasswordUtil.getPassthroughHash(PERF_DEFAULT_PWD, orgKeyInfo.encryptionKey),
						orgKeyInfo.encryptionKeyId)).execute().body();
				pollingJPTToken = passwordChangeResult.JPT;
			} else {
				//Send unlock account request
				JPTResult enableUserResult = accountService.pwdUnlock(passwordIsReady.JPT).execute().body();
				pollingJPTToken = enableUserResult.JPT;
			}

			//Poll for reset or unlock result. Simulating UI behavior, where it poll every 8 seconds. Error message will return if it polled 35 times without FINISH status
			PasswordPoll pollingResult;
			pollingResult = accountService.pwdPoll(pollingJPTToken).execute().body();
			int pollingCount = 0;
			while (!pollingResult.state.equals("FINISHED")) {
				if (++pollingCount > 35) {
					throw new IllegalStateException("Failed while " + goalName + " with code for " + username + " using" + successMsg.split("with", 2)[1] +
							" Polling result for 35 times without receiving a finished message.");
				} else if (pollingResult.JPT == null) {
					throw new IllegalStateException("Failed while " + goalName +" with code for " + username + " using" + successMsg.split("with", 2)[1] +
							" Polling returns error message: " + pollingResult.statusMessage);
				}
				Thread.sleep(8000);
				pollingResult = accountService.pwdPoll(pollingResult.JPT).execute().body();
			}

			log.info(successMsg);
			return true;

		} catch (NullPointerException e) {
			log.error("Failed while " + goalName + " for " + username + ". Server response is missing required parameters.", e);
		} catch (IOException e) {
			log.error("Failed while " + goalName + " for " + username + ". Cannot send request.", e);
		} catch (InterruptedException e) {
			log.error("Failed while " + goalName + " for " + username + ". Cannot sleep the thread wile polling for " + goalName + " results.", e);
		} catch (IllegalStateException e){
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error("Failed while " + goalName + " for " + username + ".", e);
		}

		return false;
	}
}
