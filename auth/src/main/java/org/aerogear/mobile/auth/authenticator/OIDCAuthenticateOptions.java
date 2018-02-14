package org.aerogear.mobile.auth.authenticator;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import org.aerogear.mobile.auth.authenticator.AuthenticateOptions;

public class OIDCAuthenticateOptions implements AuthenticateOptions {

    private final Activity fromActivity;
    private final int resultCode;

    public OIDCAuthenticateOptions( final Activity fromActivity, final int resultCode) {
        this.fromActivity = fromActivity;
        this.resultCode = resultCode;
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public int getResultCode() {
        return resultCode;
    }
}
