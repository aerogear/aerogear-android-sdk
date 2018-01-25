package org.aerogear.auth.credentials;

import net.openid.appauth.AuthState;

import org.aerogear.auth.AuthenticationException;
import org.json.JSONException;

/**
 * Credentials for OIDC based authentication
 */
public class OIDCCredentials implements ICredential {

    private final AuthState authState;

    public OIDCCredentials() {
        this.authState = new AuthState();
    }

    public OIDCCredentials(final String serialisedCredential) throws JSONException {
        this.authState = AuthState.jsonDeserialize(serialisedCredential);
    }

    public String getAccessToken() {
        return authState.getAccessToken();
    }

    public String getIdentityToken() {
        return authState.getIdToken();
    }

    public String getRefreshToken() {
        return authState.getRefreshToken();
    }

    /**
     * Returns whether this token is expired or not.
     * @return <code>true</code> if expired.
     */
    public boolean isExpired() {
        return authState.hasClientSecretExpired();
    }

    /**
     * Check whether new access token is needed.
     * @return <code>true</code> if access token is needed
     */
    public boolean getNeedsRenewal() {
        return authState.getNeedsTokenRefresh();
    }

    /**
     * Force request of new access token.
     */
    public void setNeedsRenewal() {
        authState.setNeedsTokenRefresh(true);
    }

    /**
     * Check if the user is authenticated/authorized.
     * @return <code>true</code> if the user is authenticated/authorized.
     */
    public boolean isAuthorized() {
        return authState.isAuthorized();
    }

    /**
     * Returns stringified JSON for the OIDCCredential.
     * @return Stringified JSON OIDCCredential
     */
    public String serialise() {
        return this.authState.jsonSerializeString();
    }

    /**
     * Check whether the user is authorized and not expired.
     * @return <code>true</code> if user is authorized and token is not expired.
     */
    public boolean checkValidAuth() {
        return isAuthorized() && getNeedsRenewal();
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
