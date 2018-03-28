package org.aerogear.mobile.auth.authenticator;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.app.Activity;
import android.support.annotation.NonNull;

public class DefaultAuthenticateOptions implements AuthenticateOptions {

    private final Activity fromActivity;
    private final int resultCode;
    private final boolean skipCertificatePinningCheck;

    public DefaultAuthenticateOptions(@NonNull final Activity fromActivity, final int resultCode) {
        this(fromActivity, resultCode, false);
    }

    private DefaultAuthenticateOptions(@NonNull final Activity fromActivity, final int resultCode,
                    final boolean skipCertificatePinningCheck) {
        this.fromActivity = nonNull(fromActivity, "fromActivity");
        this.resultCode = resultCode;
        this.skipCertificatePinningCheck = skipCertificatePinningCheck;
    }

    static class Builder {
        private Activity fromActivity;
        private int resultCode;
        private boolean skipCertificatePinningCheck = false;

        public Builder() {}

        public Builder setFromActivity(final Activity fromActivity) {
            this.fromActivity = fromActivity;
            return this;
        }

        public Builder setResultCode(final int resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder setSkipCertificatePinningChecks(final boolean skipCertificatePinningCheck) {
            this.skipCertificatePinningCheck = skipCertificatePinningCheck;
            return this;
        }

        public DefaultAuthenticateOptions build() {
            return new DefaultAuthenticateOptions(fromActivity, resultCode,
                            skipCertificatePinningCheck);
        }
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public int getResultCode() {
        return resultCode;
    }

    public boolean getSkipCertificatePinningChecks() {
        return skipCertificatePinningCheck;
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
