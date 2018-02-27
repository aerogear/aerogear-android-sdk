package org.aerogear.mobile.auth.configuration;

import android.net.Uri;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * This represents an authentication config provided by the developer.
 */
public class AuthServiceConfiguration {

    /**
     * The redirect uri for the developers app.
     */
    private final Uri redirectUri;

    /**
     * Specify the minimum time between requests to get the JWKS (Json web key set) in minutes.
     * The default value is 1440 (1 day).
     */
    private final int minTimeBetweenJwksRequests;

    /**
     * Builds a new AuthServiceConfiguration object.
     *
     * @param builder {@link AuthConfigurationBuilder}
     */
    private AuthServiceConfiguration(final AuthConfigurationBuilder builder) {
        this.redirectUri = builder.redirectUri;
        this.minTimeBetweenJwksRequests = builder.minTimeBetweenJwksRequests;
    }

    /**
     * @return the redirect uri for the developers app.
     */
    public Uri getRedirectUri() {
        return redirectUri;
    }

    /**
     * @return The minimum time between Json web key set requests. In minutes. Default value is 1440 (1 day).
     */
    public int getMinTimeBetweenJwksRequests() {
        return minTimeBetweenJwksRequests;
    }

    /**
     * Builds and returns an AuthServiceConfiguration object.
     */
    public static class AuthConfigurationBuilder {
        private Uri redirectUri;
        private int minTimeBetweenJwksRequests = 24 * 60;

        public AuthConfigurationBuilder() {
        }

        /**
         * Allow specify the value of the redirect uri
         *
         * @param redirectUri a new redirectUri value
         * @return the builder instance
         */
        public AuthConfigurationBuilder withRedirectUri(final String redirectUri) {
            this.redirectUri = Uri.parse(nonNull(redirectUri, "redirectUri"));
            return this;
        }

        public AuthConfigurationBuilder withMinTimeBetweenJwksRequests(final int minTimeBetweenJwksRequests) {
            this.minTimeBetweenJwksRequests = minTimeBetweenJwksRequests;
            return this;
        }

        public AuthServiceConfiguration build() {
            return new AuthServiceConfiguration(this);
        }
    }

}
