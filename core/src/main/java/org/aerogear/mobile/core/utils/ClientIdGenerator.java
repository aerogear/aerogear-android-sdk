package org.aerogear.mobile.core.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.core.MobileCore;

/**
 * Helper for generating ID's
 */
public final class ClientIdGenerator {

    private final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    private final static String STORAGE_KEY = "metrics-sdk-installation-id";

    /**
     * Utility classes are not meant to be instantiated.
     */
    private ClientIdGenerator() {}

    /**
     * Get or create the client ID that identifies a device as long as the user doesn't reinstall
     * the app or delete the app storage. A random UUID is created and stored in the application
     * shared preferences.
     *
     * @param context Application context
     * @return Client ID
     */
    public static String getOrCreateClientId(final Context context) {
        final SharedPreferences preferences =
                        context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);

        String clientId = preferences.getString(STORAGE_KEY, null);

        if (clientId == null) {
            clientId = UUID.randomUUID().toString();

            MobileCore.getInstance().getLogger().info("Generated a new client ID: " + clientId);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORAGE_KEY, clientId);
            editor.apply();
        }

        return clientId;
    }
}
