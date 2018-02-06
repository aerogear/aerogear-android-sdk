package org.aerogear.auth.utils;

import android.util.Log;

import org.aerogear.mobile.core.http.HttpResponse;

public class LogoutCompleteHandler implements Runnable {

    private final HttpResponse response;
    private final AuthStateManager authStateManager;
    private static final String TAG = LogoutCompleteHandler.class.getName();

    public LogoutCompleteHandler(final HttpResponse response, final AuthStateManager authStateManager) {
        this.response = response;
        this.authStateManager = authStateManager;
    }

    private void nullifyAuthState() {
        authStateManager.save(null);
    }


    @Override
    public void run() {
        if (response.getStatus() == 200) {
            nullifyAuthState();
        } else {
            Log.w(TAG, response.getStatus() + " " + response.stringBody());
        }
    }
}
