package org.aerogear.auth;

import android.net.Uri;

/**
 * This represents an authentication config provided by the developer.
 */
public class AuthConfiguration {
    private final Uri redirectUri;


    protected AuthConfiguration(final Uri redirectUri) {
        this.redirectUri = redirectUri;
    }

    static class Builder {
        protected Uri redirectUri;

        protected Builder() {}

        Builder redirectUri(final String redirectUri) {
            this.redirectUri = Uri.parse(redirectUri);
            return this;
        }

        AuthConfiguration build() {
            return new AuthConfiguration(
                this.redirectUri
            );
        }
    }

    public Uri getRedirectUri() {
        return redirectUri;
    }

}
