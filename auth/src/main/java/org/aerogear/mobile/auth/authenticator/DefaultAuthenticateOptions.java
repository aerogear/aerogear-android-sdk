package org.aerogear.mobile.auth.authenticator;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.app.Activity;
import android.support.annotation.NonNull;

public class DefaultAuthenticateOptions implements AuthenticateOptions {

    private final Activity fromActivity;
    private final int resultCode;

    public DefaultAuthenticateOptions(@NonNull final Activity fromActivity, final int resultCode) {
        this.fromActivity = nonNull(fromActivity, "fromActivity");
        this.resultCode = resultCode;
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public int getResultCode() {
        return resultCode;
    }
}
