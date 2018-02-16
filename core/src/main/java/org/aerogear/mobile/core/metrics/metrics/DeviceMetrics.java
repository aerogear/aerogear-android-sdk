package org.aerogear.mobile.core.metrics.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Collects device metrics
 */
public class DeviceMetrics implements Metrics {

    private final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    private final static String STORAGE_KEY = "metrics-sdk-installation-id";

    private final String clientId;
    private final String platform;
    private final String platformVersion;

    public DeviceMetrics(final Context context) {
        this.clientId = getOrCreateClientId(context);
        this.platform = "android";
        this.platformVersion = String.valueOf(Build.VERSION.SDK_INT);
    }

    @Override
    public String identifier() {
        return "device";
    }

    @Override
    public Map<String, String> data() {
        Map<String, String> data = new HashMap<>();
        data.put("clientId", clientId);
        data.put("platform", platform);
        data.put("platformVersion", platformVersion);
        return data;
    }

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't
     * reinstall the app or delete the app storage. A random UUID is created and stored in the
     * application shared preferences.
     *
     * @param context Application context
     * @return Client ID
     */
    private String getOrCreateClientId(final Context context) {
        final SharedPreferences preferences = context
            .getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);

        String clientId = preferences.getString(STORAGE_KEY, null);

        if (clientId == null) {
            clientId = UUID.randomUUID().toString();

            MobileCore.getLogger().info("Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORAGE_KEY, clientId);
            editor.apply();
        }

        return clientId;
    }

}
