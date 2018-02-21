package org.aerogear.mobile.auth;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.auth.credentials.OIDCCredentials;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Saves, retrieves and delete a token.
 */
public class AuthStateManager {

    private static AuthStateManager instance = null;
    private static final String STORE_NAME = "org.aerogear.android.auth.AuthState";
    private static final String KEY_STATE = "state";

    private final SharedPreferences prefs;

    private AuthStateManager(final Context context) {
        this.prefs = nonNull(context, "context").getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
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
     * @throws IllegalStateException
     */
    public synchronized void save(final OIDCCredentials authState) {
        if (authState == null) {
            clear();
        } else {
            SharedPreferences.Editor e = prefs.edit().putString(KEY_STATE, authState.serialize());
            if(!e.commit()) {
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

    static AuthStateManager getInstance(final Context context) {
        if (instance == null) {
            instance = new AuthStateManager(nonNull(context, "context"));
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
