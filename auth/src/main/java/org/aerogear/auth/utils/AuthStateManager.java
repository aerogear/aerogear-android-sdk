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

    private SharedPreferences mPrefs;

    public AuthStateManager(Context context) {
        this.mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Reads credentials from storage.
     * @return OIDCCredentials
     */
    public OIDCCredentials read() {
        String currentState = mPrefs.getString(KEY_STATE, null);
        if (currentState == null) {
            return new OIDCCredentials();
        }
        try {
            return new OIDCCredentials(currentState);
        } catch (JSONException ex) {
            return new OIDCCredentials();
        }
    }

    /**
     * Saves a token
     * @param authState token to be saved
     */
    public void write(OIDCCredentials authState) {
        if (authState == null) {
            clear();
        } else {
            if(!mPrefs.edit().putString(KEY_STATE, authState.serialise()).commit()) {
                throw new IllegalStateException("Failed to update state from shared preferences");
            }
        }
    }

    /**
     * Deletes a token
     */
    public void clear() {
        if (!mPrefs.edit().remove(KEY_STATE).commit()) {
            throw new IllegalStateException("Failed to clear state from shared preferences");
        }
    }
}
