package org.aerogear.mobile.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.core.MobileCore;

import java.util.UUID;

/**
 * Helper for generating ID's
 */
public class AppIdGenerator {

    private final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    private final static String STORAGE_KEY = "metrics-sdk-installation-id";

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't
     * reinstall the app or delete the app storage. A random UUID is created and stored in the
     * application shared preferences.
     *
     * @param context Application context
     * @return Client ID
     */
    public static String getOrCreateClientId(final Context context) {
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
