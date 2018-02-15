package org.aerogear.mobile.metrics;

import android.content.Context;
import android.content.SharedPreferences;

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
    public final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    public final static String STORAGE_KEY = "metrics-sdk-installation-id";

    private final static String MODULE_NAME = "metrics";
    private final static String TAG = "AEROGEAR/METRICS";

    private HttpServiceModule httpService;
    private Logger logger;

    private String appVersion = null;
    private String metricsUrl = null;

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't
     * reinstall the app or delete the app storage. A random UUID is created and stored in the
     * application shared preferences.
     *
     * Can be overridden to provide a different implementation for identification.
     *
     * @param context Android app context
     * @return String Client ID
     */
    protected String getOrCreateClientId(final Context context) {
        final SharedPreferences preferences = context
            .getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);

        String clientId = preferences.getString(STORAGE_KEY, null);
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();

            logger.info(TAG, "Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORAGE_KEY, clientId);
            editor.commit();
        }

        return clientId;
    }

    /**
     * This method is called to create the JSON object containing the metrics data.
     * Can be overridden to add more data points.
     *
     * @param context Android app context
     * @return JSONObject Metrics data
     * @throws JSONException when any of the data results in invalid JSON
     */
    protected JSONObject metricsData(final Context context) throws JSONException {
        final JSONObject result = new JSONObject();
        result.put("clientId", getOrCreateClientId(context));
        result.put("appId", context.getPackageName());
        result.put("appVersion", appVersion);
        result.put("sdkVersion", MobileCore.getSdkVersion());
        return result;
    }

    /**
     * Send the metrics data to the server. The data is contained in a JSON object with the
     * following properties: clientId, appId, sdkVersion and appVersion
     *
     * Should not be overridden. Users can change the target URL in mobile-services.json
     *
     * @param context Android application context
     */
    public final void init(final Context context) {
        try {
            final JSONObject data = metricsData(context);

            // Send request to backend
            HttpRequest request = httpService.newRequest();
            request.post(metricsUrl, data.toString().getBytes());
            HttpResponse response = request.execute();

            // Async response handling
            response.onComplete(() -> {
                if (response.getStatus() != 200) {
                    logger.error(TAG, "Error sending metrics data");
                }
            });
        } catch (JSONException e) {
            logger.error(TAG, e);
        }
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        metricsUrl = serviceConfiguration.getUrl();
        appVersion = core.getAppVersion();
        httpService = core.getHttpLayer();
        logger = MobileCore.getLogger();
    }

    @Override
    public String type() {
        return MODULE_NAME;
    }

    @Override
    public void destroy() {
    }
}
