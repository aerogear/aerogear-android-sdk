package org.aerogear.mobile.metrics.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.metrics.MetricsService;
import org.aerogear.mobile.metrics.interfaces.MetricsProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class DefaultMetricsProvider extends MetricsProvider {
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
            .getSharedPreferences(MetricsService.STORAGE_NAME, Context.MODE_PRIVATE);

        String clientId = preferences.getString(MetricsService.STORAGE_KEY, null);
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();

            MobileCore.getLogger().info(MetricsService.TAG, "Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(MetricsService.STORAGE_KEY, clientId);
            editor.commit();
        }

        return clientId;
    }

    /**
     * Get the user app version from the package manager
     *
     * @param context Android application context
     * @return String app version name
     */
    private String getAppVersion(final Context context) throws JSONException {
        try {
            return context
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Wrap in Initialization exception
            throw new JSONException(e.getMessage());
        }
    }

    @Override
    public String namespace() {
        return null;
    }

    @Override
    public JSONObject metrics(final Context context) throws JSONException {
        final JSONObject result = super.metrics(context);
        result.put("clientId", getOrCreateClientId(context));
        result.put("appId", context.getPackageName());
        result.put("appVersion", getAppVersion(context));
        result.put("sdkVersion", MobileCore.getSdkVersion());
        result.put("platform", "android");
        result.put("platformVersion", Build.VERSION.SDK_INT);
        return result;
    }
}
