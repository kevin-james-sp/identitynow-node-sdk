package sailpoint.services.idn.sdk.services;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sailpoint.featureflag.FeatureFlagClient;
import com.sailpoint.featureflag.impl.LDFeatureFlagClient;
import com.sailpoint.featureflag.impl.LdPatchBuilder;
import com.sailpoint.featureflag.model.Environment;
import com.sailpoint.featureflag.model.FeatureUser;
import com.sailpoint.featureflag.model.LdFeatureFlag;
import com.sailpoint.utilities.JsonUtil;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sailpoint.services.idn.sdk.ClientCredentials;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sailpoint.featureflag.impl.LDFeatureFlagClient.ENVIRONMENT_TEST;
import static com.sailpoint.featureflag.util.ValidationUtil.require;
import static java.util.Objects.requireNonNull;

/**
 * This class is based off of the analog in cloud-test-services. Due to the closed source nature of Launch Darkly,
 * this class breaks some paradigms in AbleMan. Namely, it uses gson instead of Jackson, and includes the use of apache
 * library items. These exceptions should be rare and only used when necessary. This class is used to manipulate feature
 * flags programmatically. Caution should be used because these changes can impact pods outside of dev01/dev02.
 */

public class FeatureFlagService {
	private static final Logger log = LogManager.getLogger(FeatureFlagService.class);

	private static LDFeatureFlagClient _ldFeatureFlagClient;
	private static FeatureUser _featureUser;
	private static ClientCredentials envCreds = EnvironmentCredentialer.getEnvironmentCredentials();
	private static final String pod = envCreds.getUserIntUrl().substring("https://".length(), envCreds.getUserIntUrl().indexOf("."));
	private static CloseableHttpClient _client;

	Gson gson = new GsonBuilder()
			.setFieldNamingStrategy(new UnderscoreFieldNamingStrategy())
			//.registerTypeAdapter(Date.class, new JsonDateDeserializer())
			//.registerTypeAdapter(L10nString.class, new L10nStringDeserializer())
			//.registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeSerializer())
			.create();

	public FeatureFlagService(UserInterfaceSession uiSession){
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(50);
		cm.setDefaultMaxPerRoute(10);

		RequestConfig rc = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		_client = HttpClients.custom().setDefaultRequestConfig(rc).setConnectionManager(cm).build();
		_featureUser = FeatureFlagClient.buildFeatureUser(pod, envCreds.getOrgName(), "e2e");
		_ldFeatureFlagClient =
				new LDFeatureFlagClient(LDFeatureFlagClient.PROJECT_IDENTITY_NOW,
						LDFeatureFlagClient.ENVIRONMENT_TEST,
						_client);
		_ldFeatureFlagClient.start();
	}

	public void setFlagForOrg(boolean value, Enum<?> key){
		if(value){
			removeOrgFromExcludeList(key);
			addOrgToIncludeList(key);
		}
		else{
			removeOrgFromIncludeList(key);
			addOrgToExcludeList(key);
		}
	}

	public void stop(){
		_ldFeatureFlagClient.stop();
	}

	public Map<String, Object> listFlags() {
		if (null != _ldFeatureFlagClient) {
			return _ldFeatureFlagClient.listFlags(_featureUser);
		}
		return null;
	}

	public boolean getBoolean(Enum<?> key) {
		return _ldFeatureFlagClient.getBoolean(key, _featureUser, false);
	}

	/**
	 * API used to selectively disable a feature flag at the org level.
	 *
	 * @param key the feature flag to disable.
	 */
	public void addOrgToExcludeList(Enum<?> key) {
		JsonObject flagJson = getFlagJson(key);
		String orgName = envCreds.getOrgName();
		Environment testEnvironment = getFlagEnvironment(flagJson);
		Set<String> orgExcludeList = testEnvironment.getRuleOrgs(true);
		if(orgExcludeList.contains(orgName)) {
			// No change required.
			return;
		}

		orgExcludeList.add(orgName);
		LdPatchBuilder patchBuilder = new LdPatchBuilder(flagJson, LDFeatureFlagClient.ENVIRONMENT_TEST);
		patchBuilder.setOrgList(gson.fromJson(gson.toJson(orgExcludeList), JsonArray.class), true);
		List<JsonElement> patch = patchBuilder.build();
		log.info("Applying feature flag patch: " + gson.toJson(patch));
		patchFeatureFlag(key, patch, false);
	}

	/**
	 * API used to remove an org from the list of orgs a feature flag is disabled for.
	 *
	 * @param key the feature flag to enable.
	 */
	public void removeOrgFromExcludeList(Enum<?> key) {
		JsonObject flagJson = getFlagJson(key);
		String orgName = envCreds.getOrgName();
		Environment testEnvironment = getFlagEnvironment(flagJson);
		Set<String> orgExcludeList = testEnvironment.getRuleOrgs(true);
		if(!orgExcludeList.contains(orgName)) {
			// No change required.
			return;
		}

		orgExcludeList.remove(orgName);
		LdPatchBuilder patchBuilder = new LdPatchBuilder(flagJson, LDFeatureFlagClient.ENVIRONMENT_TEST);
		patchBuilder.setOrgList(gson.fromJson(gson.toJson(orgExcludeList), JsonArray.class), true);
		List<JsonElement> patch = patchBuilder.build();
		log.info("Applying feature flag patch: " + gson.toJson(patch));

		patchFeatureFlag(key, patch, true);
	}

	/**
	 * API used to selectively enable a feature flag at the org level.
	 *
	 * @param key the feature flag to enable.
	 */
	public void addOrgToIncludeList(Enum<?> key) {
		JsonObject flagJson = getFlagJson(key);
		String orgName = envCreds.getOrgName();
		Environment testEnvironment = getFlagEnvironment(flagJson);
		Set<String> orgIncludeList = testEnvironment.getRuleOrgs(false);
		if(orgIncludeList.contains(orgName)) {
			// No change required.
			return;
		}

		orgIncludeList.add(orgName);
		LdPatchBuilder patchBuilder = new LdPatchBuilder(flagJson, LDFeatureFlagClient.ENVIRONMENT_TEST);
		patchBuilder.setOrgList(gson.fromJson(gson.toJson(orgIncludeList), JsonArray.class), false);
		List<JsonElement> patch = patchBuilder.build();
		log.info("Applying feature flag patch: " + gson.toJson(patch));

		patchFeatureFlag(key, patch, true);
	}

	/**
	 * API used to remove an org from the list of orgs a feature flag is enabled for.
	 *
	 * @param key the feature flag to disable.
	 */
	public void removeOrgFromIncludeList(Enum<?> key) {
		JsonObject flagJson = getFlagJson(key);
		String orgName = envCreds.getOrgName();
		Environment testEnvironment = getFlagEnvironment(flagJson);
		Set<String> orgIncludeList = testEnvironment.getRuleOrgs(false);
		if(!orgIncludeList.contains(orgName)) {
			// No change required.
			return;
		}

		orgIncludeList.remove(orgName);
		LdPatchBuilder patchBuilder = new LdPatchBuilder(flagJson, LDFeatureFlagClient.ENVIRONMENT_TEST);
		patchBuilder.setOrgList(gson.fromJson(gson.toJson(orgIncludeList), JsonArray.class), false);
		List<JsonElement> patch = patchBuilder.build();
		log.info("Applying feature flag patch: " + gson.toJson(patch));

		patchFeatureFlag(key, patch, false);
	}

/*	*//**
	 * API used to selectively enable pendo suppress feature flags at the org level.
	 *//*
	public void suppressPendo() {
		addOrgToIncludeList(FEATURE_FLAGS.PENDO_SUPPRESS);
	}

	*//**
	 * API used to selectively disable pendo suppress feature flags at the org level.
	 *//*
	public void restorePendo() {
		removeOrgFromIncludeList(FEATURE_FLAGS.PENDO_SUPPRESS);
	}*/

	private JsonObject getFlagJson(Enum<?> key) {
		Map<String, Object> currentFlags = listFlags();
		if(!currentFlags.containsKey(key.name())) {
			throw new RuntimeException("Feature flag "+key+" does not exist.");
		}

		JsonObject flagJson = _ldFeatureFlagClient.getFlagDetail(key.name()).getAsJsonObject();
		log.info("LD Feature flag JSON: "+ gson.toJson(flagJson));
		return flagJson;
	}

	private Environment getFlagEnvironment(JsonObject flagJson) {
		LdFeatureFlag featureFlag = gson.fromJson(gson.toJson(flagJson), LdFeatureFlag.class);
		requireNonNull(featureFlag, "featureFlag must not be null!");

		Map<String, Environment> environments = featureFlag.getEnvironments();
		requireNonNull(environments, "environments must not be null!");
		if (!environments.containsKey(ENVIRONMENT_TEST)) {
			throw new IllegalStateException("LaunchDarkly feature flag JSON missing test environment.");
		}

		Environment testEnvironemnt = environments.get(ENVIRONMENT_TEST);
		requireNonNull(testEnvironemnt, "testEnvironemnt must not be null!");
		return testEnvironemnt;
	}

	/**
	 * Updates the featureflag taking into account launch darkly rate limiting upto 130 seconds.
	 * @param key - flag to be updated.
	 * @param patchJson - fetaureflag json object.
	 * @param expectedValue - flag state to wait for.
	 * @return boolean - True if the expectedValue matches the flag's current value, false otherwise.
	 */
	private void patchFeatureFlag(Enum<?> key, List<JsonElement> patchJson, boolean expectedValue) {
		int currentWaitSeconds = 1;
		int maxWaitSeconds = 90;
		_ldFeatureFlagClient.updateFlag(key.name(), patchJson);
		while( (_ldFeatureFlagClient.getBoolean(key, _featureUser, !expectedValue) != expectedValue) && (currentWaitSeconds < maxWaitSeconds)) {
			try{
				Thread.sleep(currentWaitSeconds * 1000);
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
			currentWaitSeconds = (currentWaitSeconds * 2) + 5;
			// second check to take into account update latency
			if (_ldFeatureFlagClient.getBoolean(key, _featureUser, !expectedValue) != expectedValue) {
				_ldFeatureFlagClient.updateFlag(key.name(), patchJson);
			}
		}

		log.info("Waited "+currentWaitSeconds+" seconds for fleature flag "+key+" to be updated");
	}

	public void setDefault(boolean bool, Enum<?> key) {
		require("fallthroughValue", bool);
		require("key", key);
		List<JsonElement> patch =
				new LdPatchBuilder(getFlagJson(key), _ldFeatureFlagClient.getEnvironment())
						.setFallthrough(bool)
						.build();
		log.info("Applying feature flag patch:\n" + JsonUtil.toJsonPretty(patch));
		_ldFeatureFlagClient.updateFlag(key.toString(), patch);
	}

	/**
	 * Handles mapping our underscore field naming convention to JSON fields.
	 * Custom {@code FileNamingStrategy} which ignores a leading underscore
	 * in field namings when marshalling to JSON attribute names.
	 */
	private static class UnderscoreFieldNamingStrategy implements FieldNamingStrategy {
		@Override
		public String translateName(Field field) {
			if (field.getName().startsWith("_")) {
				return field.getName().substring(1);
			}

			return field.getName();
		}
	}
}
