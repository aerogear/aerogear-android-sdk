package org.aerogear.mobile.auth.configuration;

import android.net.Uri;

/**
 * This represents an authentication config provided by the developer.
 */
public class AuthServiceConfiguration {
    /**
     * The redirect uri for the developers app.
     */
    private final Uri redirectUri;

    /**
     * If self-signed certificate should be allowed.
     */
    private final boolean allowSelfSignedCertificate;


    /**
     * Builds a new AuthServiceConfiguration object.
     *
     * @param builder {@link AuthConfigurationBuilder}
     */
    private AuthServiceConfiguration(AuthConfigurationBuilder builder) {
        this.redirectUri = builder.redirectUri;
        this.allowSelfSignedCertificate = builder.allowSelfSignedCert;
    }

    /**
     * Builds and returns an AuthServiceConfiguration object.
     */
    public static class AuthConfigurationBuilder {
        private Uri redirectUri;
        private boolean allowSelfSignedCert;

        public AuthConfigurationBuilder() {}

        /**
         * Allow specify the value of the redirect uri
         * @param redirectUri
         * @return
         */
        public AuthConfigurationBuilder withRedirectUri(String redirectUri) {
            this.redirectUri = Uri.parse(redirectUri);
            return this;
        }

        /**
         * Specify if self sign certificate is allow.
         * NOTE: this is not for production use and it should only be used for testing purpose.
         * @param allowSelfSignedCert
         * @return
         */
        public AuthConfigurationBuilder allowSelfSignedCertificate(boolean allowSelfSignedCert) {
            this.allowSelfSignedCert = allowSelfSignedCert;
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
     * @return If self signed certificate is allowed
     */
    public boolean isAllowSelfSignedCertificate() {
        return allowSelfSignedCertificate;
    }

}
