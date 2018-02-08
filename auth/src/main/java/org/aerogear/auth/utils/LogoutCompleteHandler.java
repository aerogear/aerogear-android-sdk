package org.aerogear.auth.utils;

import android.util.Log;

import org.aerogear.mobile.core.http.HttpResponse;

/**
 * The callback to handle the response from a logout request.
 */
public class LogoutCompleteHandler implements Runnable {

    private final HttpResponse response;
    private final AuthStateManager authStateManager;
    private static final String TAG = LogoutCompleteHandler.class.getName();

    /**
     * Creates a new LogoutCompleteHandler object.
     *
     * @param response
     * @param authStateManager
     */
    public LogoutCompleteHandler(final HttpResponse response, final AuthStateManager authStateManager) {
        this.response = response;
        this.authStateManager = authStateManager;
    }

    /**
     * Writes null to the AuthState.
     */
    private void nullifyAuthState() {
        authStateManager.save(null);
    }


    /**
     * Handles logout response.
     */
    @Override
    public void run() {
        if (response.getStatus() == 200) {
            nullifyAuthState();
        } else {
            Log.w(TAG, response.getStatus() + " " + response.stringBody());
        }
    }
}
