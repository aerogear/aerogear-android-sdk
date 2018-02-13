package org.aerogear.mobile.auth;

import android.net.Uri;

/**
 * This represents an authentication config provided by the developer.
 */
public class AuthConfiguration {
    /**
     * The redirect uri for the developers app.
     */
    private final Uri redirectUri;


    /**
     * Builds a new AuthConfiguration object.
     *
     * @param builder {@link AuthConfigurationBuilder}
     */
    private AuthConfiguration(AuthConfigurationBuilder builder) {
        this.redirectUri = builder.redirectUri;
    }

    /**
     * Builds and returns an AuthConfiguration object.
     */
    public static class AuthConfigurationBuilder {
        private Uri redirectUri;

        public AuthConfigurationBuilder() {}

        public AuthConfigurationBuilder withRedirectUri(String redirectUri) {
            this.redirectUri = Uri.parse(redirectUri);
            return this;
        }

        public AuthConfiguration build() {
            return new AuthConfiguration(this);
        }
    }

    /**
     * @return the redirect uri for the developers app.
     */
    public Uri getRedirectUri() {
        return redirectUri;
    }

}
