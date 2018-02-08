package org.aerogear.auth;

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
     * @param redirectUri
     */
    protected AuthConfiguration(final Uri redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * Builds and returns a AuthConfiguration object.
     */
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

    /**
     * @return the redirect uri for the developers app.
     */
    public Uri getRedirectUri() {
        return redirectUri;
    }

}
