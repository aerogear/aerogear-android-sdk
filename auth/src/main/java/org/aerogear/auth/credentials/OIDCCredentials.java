package org.aerogear.auth.credentials;

import net.openid.appauth.AuthState;

import org.aerogear.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Credentials for OIDC based authentication
 */
public class OIDCCredentials implements ICredential {

    private final AuthState authState;
    private final IIntegrityCheckParameters integrityCheckParameters;

    public OIDCCredentials(final String serialisedCredential, final IIntegrityCheckParameters integrityCheckParameters) throws JSONException {
        this.authState = AuthState.jsonDeserialize(serialisedCredential);
        this.integrityCheckParameters = integrityCheckParameters;
    }

    public OIDCCredentials() {
        this.authState = new AuthState();
        this.integrityCheckParameters = new IntegrityCheckParameters(null, null);
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

    public IIntegrityCheckParameters getIntegrityCheckParameters() { return this.integrityCheckParameters; }

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
    public String serialize() throws JSONException {
        return new JSONObject()
            .put("authState", this.authState.jsonSerializeString())
            .put("integrityCheck", this.integrityCheckParameters.serialize())
            .toString();
    }

    public static OIDCCredentials deserialize(String serializedCredential) throws JSONException {
        JSONObject jsonCredential = new JSONObject(serializedCredential);
        String serializedAuthState = jsonCredential.getString("authState");
        String serializedIntegrityChecks = jsonCredential.getString("integrityCheck");
        IntegrityCheckParameters icParams = IntegrityCheckParameters.deserialize(serializedIntegrityChecks);
        return new OIDCCredentials(serializedAuthState, icParams);
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
