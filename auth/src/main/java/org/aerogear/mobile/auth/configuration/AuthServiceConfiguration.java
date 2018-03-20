package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.net.Uri;

/**
 * This represents an authentication config provided by the developer.
 */
public class AuthServiceConfiguration {

    /**
     * The default scope.
     */
    private final String SCOPE_OPENID;

    /**
     * The redirect uri for the developers app.
     */
    private final Uri redirectUri;

    /**
     * The OIDC scopes to use in the auth request.
     */
    private final String scopes;

    /**
     * Specify the minimum time between requests to get the JWKS (Json web key set) in minutes. The
     * default value is 1440 (1 day).
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
        this.scopes = builder.scopes;
        this.SCOPE_OPENID = builder.SCOPE_OPENID;
    }

    /**
     * Builds and returns an AuthServiceConfiguration object.
     */
    public static class AuthConfigurationBuilder {
        private Uri redirectUri;
        private int minTimeBetweenJwksRequests = 24 * 60;
        private String scopes;
        private final String SCOPE_OPENID = "openid";

        public AuthConfigurationBuilder() {}

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

        /**
         * Allow specifying the OIDC scopes of the auth request
         *
         * @param scopes the OIDC scopes
         * @return the builder instance
         */
        public AuthConfigurationBuilder withScopes(final String scopes) {
            this.scopes = scopes;
            return this;
        }

        public AuthConfigurationBuilder withMinTimeBetweenJwksRequests(
                        final int minTimeBetweenJwksRequests) {
            this.minTimeBetweenJwksRequests = minTimeBetweenJwksRequests;
            return this;
        }

        public AuthServiceConfiguration build() {
            return new AuthServiceConfiguration(this);
        }
    }

    /**
     * @return the redirect uri for the developers app.
     */
    public Uri getRedirectUri() {
        return redirectUri;
    }

    /**
     * @return the OIDC scopes for the auth request. If no scopes are defined, the default 'openid'
     *         scope will be sent.
     */
    public String getScopes() {
        if (scopes != null) {
            return scopes;
        } else {
            return SCOPE_OPENID;
        }
    }

    /**
     * @return The minimum time between Json web key set requests. In minutes. Default value is 1440
     *         (1 day).
     */
    public int getMinTimeBetweenJwksRequests() {
        return minTimeBetweenJwksRequests;
    }

}
