package mobile.aerogear.org.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MetricsService implements ServiceModule {
    private final static String MODULE_NAME = "metrics";
    private final static String LOG_TAG = "AEROGEAR/METRICS";
    private final static String STORAGE_NAME = "mobile.aerogear.org.metrics";
    private final static String STORAGE_KEY = "metrics-sdk-installation-id";

    private HttpServiceModule httpService;
    private MetricsConfig config;
    private Logger logger;

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't
     * reinstall the app or delete the app storage. A random UUID is created and stored in the
     * application shared preferences.
     *
     * @param context Android app context
     * @return String Client ID
     */
    private String getOrCreateClientId(final Context context) {
        final SharedPreferences preferences = context
            .getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);

        String clientId = preferences.getString(STORAGE_KEY, null);
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();

            logger.info(LOG_TAG, "Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORAGE_KEY, clientId);
            editor.commit();
        }

        return clientId;
    }

    /**
     * Get the version of the app itself
     *
     * @param context Android application context
     * @return String version name
     */
    private String getAppVersion(final Context context) {
        try {
            return context
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            logger.error(LOG_TAG, e);
            return null;
        }
    }

    private byte[] getMetricsData(final Context context) throws JSONException {
        final JSONObject result = new JSONObject();
        result.put("clientId", getOrCreateClientId(context));
        result.put("appId", context.getPackageName());
        result.put("appVersion", getAppVersion(context));
        result.put("sdkVersion", BuildConfig.VERSION_NAME);

        return result.toString().getBytes();
    }

    /**
     * Send the metrics data to the server. The data is contained in a JSON object with the
     * following properties: clientId, appId, sdkVersion and appVersion
     *
     * @param context Android application context
     */
    public void init(final Context context) {
        try {
            // Send request to backend
            final HttpRequest request = httpService.newRequest();
            request.post(config.getUri(), getMetricsData(context));
            final HttpResponse response = request.execute();
            response.onComplete(() -> {
                if (response.getStatus() == 200) {
                    logger.info(LOG_TAG, "Metrics sent to server");
                } else {
                    logger.error(LOG_TAG, "Error sending metrics to server");
                }
            });
        } catch (JSONException e) {
            logger.error(LOG_TAG, e);
        }
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        config = new MetricsConfig(serviceConfiguration);
        httpService = core.getHttpLayer();
        logger = core.getLogger();
    }

    @Override
    public String type() {
        return MODULE_NAME;
    }

    @Override
    public void destroy() {
        // Not used
    }
}
