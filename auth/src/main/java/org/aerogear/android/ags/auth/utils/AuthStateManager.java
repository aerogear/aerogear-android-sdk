package org.aerogear.android.ags.auth.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.android.ags.auth.credentials.OIDCCredentials;

/**
 * Saves, retrieves and delete a token.
 */
public class AuthStateManager {

    private static AuthStateManager instance = null;
    private static final String STORE_NAME = "org.aerogear.android.auth.AuthState";
    private static final String KEY_STATE = "state";

    private final SharedPreferences prefs;

    private AuthStateManager(final Context context) {
        this.prefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Reads credentials from storage.
     * @return OIDCCredentials
     */
    public OIDCCredentials load() {
        final String currentState = prefs.getString(KEY_STATE, null);
        if (currentState == null) {
            return new OIDCCredentials();
        }
        return OIDCCredentials.deserialize(currentState);
    }

    /**
     * Saves a token
     * @param authState token to be saved
     * @throws IllegalArgumentException
     */
    public synchronized void save(final OIDCCredentials authState) {
        if (authState == null) {
            clear();
        } else {
            SharedPreferences.Editor e = prefs.edit();
            SharedPreferences.Editor bleh = e.putString(KEY_STATE, authState.serialize());
            if(!bleh.commit()) {
                throw new IllegalStateException("Failed to update state from shared preferences");
            }
        }
    }

    /**
     * Deletes a token
     * @throws IllegalArgumentException
     */
    public synchronized void clear() {
        if (!prefs.edit().remove(KEY_STATE).commit()) {
            throw new IllegalStateException("Failed to clear state from shared preferences");
        }
    }

    public static AuthStateManager getInstance(final Context context) {
        if (instance == null) {
            instance = new AuthStateManager(context);
        }
        return instance;
    }

    public static AuthStateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Context has not previously been provided. Cannot initialize without Context.");
        }
        return instance;
    }
}
