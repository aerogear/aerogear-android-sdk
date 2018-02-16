package org.aerogear.mobile.core.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import org.aerogear.mobile.core.MobileCore;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Collects some default metrics about the App and SDK versions as well as the
 * client ID
 */
public class DefaultMetrics {
    private final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    private final static String STORAGE_KEY = "metrics-sdk-installation-id";

    private final String clientId;
    private final String appId;
    private final String appVersion;
    private final String sdkVersion;
    private final String platform;
    private final String platformVersion;

    public DefaultMetrics(final Context context) {
        this.clientId = getOrCreateClientId(context);
        this.appId = context.getPackageName();
        this.appVersion = getAppVersion(context);
        this.sdkVersion = MobileCore.getSdkVersion();
        this.platform = "android";
        this.platformVersion = String.valueOf(Build.VERSION.SDK_INT);
    }

    /**
     * Return default metrics in JSON format
     * @return JSONObject with metrics data
     * @throws JSONException
     */
    public Map<String, String> getDefaultMetrics() {
        Map<String, String>defaultMetrics = new HashMap<>();
        defaultMetrics.put("clientId", clientId);
        defaultMetrics.put("appId", appId);
        defaultMetrics.put("appVersion", appVersion);
        defaultMetrics.put("sdkVersion", sdkVersion);
        defaultMetrics.put("platform", platform);
        defaultMetrics.put("platformVersion", platformVersion);
        return defaultMetrics;
    }

    /**
     * Get the user app version from the package manager
     *
     * @param context Android application context
     * @return String app version name
     */
    private String getAppVersion(final Context context) {

        try {
            return context
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't
     * reinstall the app or delete the app storage. A random UUID is created and stored in the
     * application shared preferences.
     * <p>
     * Can be overridden to provide a different implementation for identification.
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

            MobileCore.getLogger().info("Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORAGE_KEY, clientId);
            editor.apply();
        }

        return clientId;
    }
}
