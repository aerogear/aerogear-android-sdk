package org.aerogear.mobile.auth.credentials;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

public class KeyCloakWebCredentials implements ICredential {
    private final Context ctx;
    private final Uri redirectUri;
    private final Activity fromActivity;

    public KeyCloakWebCredentials(final Context ctx, final Uri redirectUri, final Activity fromActivity) {
        this.ctx = ctx;
        this.redirectUri = redirectUri;
        this.fromActivity = fromActivity;
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public Context getCtx() {
        return ctx;
    }

    public Uri getRedirectUri() {
        return redirectUri;
    }
}
