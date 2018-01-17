package org.aerogear.auth.credentials;

import org.aerogear.auth.AbstractAuthenticator;
import org.aerogear.auth.AuthenticationException;

/**
 * Credentials for OIDC based authentication
 */
public final class OIDCCredentials implements ICredential {

    private OIDCToken identityToken;
    private OIDCToken accessToken;
    private OIDCToken refreshToken;

    /**
     * The authenticator that validated the user owning this credentials.
     * This is to be used to renew/revoke the token
     */
    private final AbstractAuthenticator authenticator;

    public OIDCCredentials(final OIDCToken identityToken, final OIDCToken accessToken, final OIDCToken refreshToken, final AbstractAuthenticator authenticator) {
        this.identityToken = identityToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authenticator = authenticator;
    }

    public OIDCToken getAccessToken() {
        return accessToken;
    }

    public OIDCToken getIdentityToken() {
        return identityToken;
    }

    public OIDCToken getRefreshToken() {
        return refreshToken;
    }

    /**
     * Returns whether this token is expired or not.
     * @return <code>true</code> if expired.
     */
    public boolean isExpired() {
        throw new IllegalStateException("Not yet implemented");
    }

    /**
     * Renew the token
     * @return
     * @throws AuthenticationException
     */
    public boolean renew() throws AuthenticationException {
        throw new IllegalStateException("Not yet implemented");
    }
}
