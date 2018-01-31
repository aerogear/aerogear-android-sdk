package org.aerogear.auth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import org.aerogear.auth.credentials.OIDCCredentials;
import org.json.JSONException;

/**
 * Saves, retrieves and delete a token.
 */
public class AuthStateManager {

    private static final String STORE_NAME = "org.aerogear.android.auth.AuthState";
    private static final String KEY_STATE = "state";

    private final SharedPreferences prefs;

    public AuthStateManager(final Context context) {
        this.prefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Reads credentials from storage.
     * @return OIDCCredentials
     */
    public OIDCCredentials read() {
        String currentState = prefs.getString(KEY_STATE, null);
        if (currentState == null) {
            return new OIDCCredentials();
        }
        try {
            return OIDCCredentials.deserialize(currentState);
        } catch (JSONException ex) {
            return new OIDCCredentials();
        }
    }

    /**
     * Saves a token
     * @param authState token to be saved
     */
    public synchronized void write(final OIDCCredentials authState) throws JSONException {
        if (authState == null) {
            clear();
        } else {
            if(!prefs.edit().putString(KEY_STATE, authState.serialize()).commit()) {
                throw new IllegalStateException("Failed to update state from shared preferences");
            }
        }
    }

    /**
     * Deletes a token
     */
    public synchronized void clear() {
        if (!prefs.edit().remove(KEY_STATE).commit()) {
            throw new IllegalStateException("Failed to clear state from shared preferences");
        }
    }
}
