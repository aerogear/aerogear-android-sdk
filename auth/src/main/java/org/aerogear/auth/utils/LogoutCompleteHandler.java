package org.aerogear.auth.utils;

import org.aerogear.mobile.core.http.HttpResponse;

public class LogoutCompleteHandler implements Runnable {

    private HttpResponse response;
    private AuthStateManager authStateManager;

    public LogoutCompleteHandler(HttpResponse response, AuthStateManager authStateManager) {
        this.response = response;
        this.authStateManager = authStateManager;
    }

    private void nullifyAuthState() {
        authStateManager.write(null);
    }


    @Override
    public void run() {
        if (response.getStatus() == 200) {
            nullifyAuthState();
        } else {
            throw new RuntimeException(response.getStatus() + " " + response.stringBody());
        }
    }
}
