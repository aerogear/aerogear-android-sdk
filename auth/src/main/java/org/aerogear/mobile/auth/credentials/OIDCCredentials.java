package org.aerogear.mobile.auth.credentials;

import android.util.Log;

import net.openid.appauth.AuthState;

import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.aerogear.mobile.core.utils.SanityCheck.nonEmpty;

/**
 * Credentials for OIDC based authentication
 */
public class OIDCCredentials {

    private static final String TAG = "OIDCCredentials";
    private static final String beginPublicKey = "-----BEGIN PUBLIC KEY-----";
    private static final String endPublicKey = "-----END PUBLIC KEY-----";

    private final AuthState authState;

    /**
     * OpenID Connect credentials containing the identity, refresh and access tokens provided on a
     * successful authentication with OpenID Connect.
     * @param serialisedCredential JSON string representation of the authState field produced by
     *                             {@link #deserialize(String)}.
     * @throws IllegalArgumentException
     */
    public OIDCCredentials(final String serialisedCredential) {
        try {
            this.authState = AuthState.jsonDeserialize(serialisedCredential);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public OIDCCredentials() {
        this.authState = new AuthState();
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
     * Verify the token and its claims against the given Keycloak configuration
     * @param keycloakConfig
     * @return
     */
    public boolean verifyClaims(JsonWebKeySet jwks, KeycloakConfiguration keycloakConfig) {
        boolean isValid = false;
        String issuer = keycloakConfig.getHostUrl();
        String audience = keycloakConfig.getClientId();
        JwksVerificationKeyResolver jwksKeyResolver = new JwksVerificationKeyResolver(jwks.getJsonWebKeys());
        // Validate and process the JWT.
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime() // require the JWT to have an expiration time
            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
            .setRequireSubject() // require the subject claim
            .setExpectedIssuer(issuer) // whom the JWT needs to have been issued by
            .setExpectedAudience(audience) // to whom the JWT is intended for
            .setVerificationKeyResolver(jwksKeyResolver)
            .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, // which is only RS256 here
                    AlgorithmIdentifiers.RSA_USING_SHA256))
            .build(); // create the JwtConsumer instance

        try {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(this.getAccessToken());
            isValid = true;
        } catch (InvalidJwtException e) {
            Log.e(TAG, "Invalid JWT provided", e);
            isValid = false;
        }
        return isValid;
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
     * @throws IllegalArgumentException
     */
    public String serialize() {
        try {
            final JSONObject jsonCredential = new JSONObject()
                .put("authState", this.authState.jsonSerializeString());
            return jsonCredential.toString();
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Return a new credential from the output of {@link #serialize()}
     * @param serializedCredential serialized credential from {@link #serialize()}
     * @return new credential
     * @throws IllegalArgumentException
     */
    public static OIDCCredentials deserialize(final String serializedCredential) {
        nonEmpty(serializedCredential, "serializedCredential");

        try {
            final JSONObject jsonCredential = new JSONObject(serializedCredential);
            final String serializedAuthState = jsonCredential.getString("authState");
            return new OIDCCredentials(serializedAuthState);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Check whether the user is authorized and not expired.
     * @return <code>true</code> if user is authorized and token is not expired.
     */
    public boolean checkValidAuth() {
        return isAuthorized() && !getNeedsRenewal();
    }

    /**
     * Renew the token
     * @return
     * @throws IllegalStateException
     */
    public boolean renew() throws AuthenticationException {
        throw new IllegalStateException("Not yet implemented");
    }
}
