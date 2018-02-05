package org.aerogear.android.ags.auth.credentials;

import android.util.Log;

import net.openid.appauth.AuthState;

import org.aerogear.android.ags.auth.AuthenticationException;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Credentials for OIDC based authentication
 */
public class OIDCCredentials implements ICredential {

    private static final String TAG = "OIDCCredentials";
    private static final String beginPublicKey = "-----BEGIN PUBLIC KEY-----";
    private static final String endPublicKey = "-----END PUBLIC KEY-----";

    private final AuthState authState;
    private final IIntegrityCheckParameters integrityCheckParameters;

    /**
     * OpenID Connect credentials containing the identity, refresh and access tokens provided on a
     * successful authentication with OpenID Connect.
     * @param serialisedCredential JSON string representation of the authState field produced by
     *                             {@link #deserialize(String)}.
     * @param integrityCheckParameters Integrity check parameters for the token.
     * @throws IllegalArgumentException
     */
    public OIDCCredentials(final String serialisedCredential, final IIntegrityCheckParameters integrityCheckParameters) {
        try {
            this.authState = AuthState.jsonDeserialize(serialisedCredential);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
        this.integrityCheckParameters = integrityCheckParameters;
    }

    public OIDCCredentials() {
        this.authState = new AuthState();
        this.integrityCheckParameters = new IntegrityCheckParameters();
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
     * Verify the authenticity of a JWT token against integrity parameters (provided config).
     * @param jwtToken The JWT token to verify.
     * @return <code>true</code> if the token integrity is good.
     */
    public boolean verifyToken(final String jwtToken) {
        return verifyToken(jwtToken,
            integrityCheckParameters.getPublicKey(),
            integrityCheckParameters.getIssuer(),
            integrityCheckParameters.getAudience());
    }

    /**
     * A function to verify the authenticity of a JWT token against a public key, expected issuer and audience.
     * @param jwtToken - The JWT Token to Verify.
     * @param publicKey - The Public Key from Keycloak, without the Begin/End tags.
     * @param issuer - The expected Issuer of the JWT
     * @param audience - The expected Audience of the JWT
     * @return <code>true</code> if the token integrity is good.
     * @throws IllegalArgumentException
     */
    public boolean verifyToken(final String jwtToken, final String publicKey, final String issuer, final String audience) {
        final String constructedPublicKey = beginPublicKey + publicKey + endPublicKey;

        // Convert the public key from a string to a Java security key
        final RsaKeyUtil utils = new RsaKeyUtil();
        final PublicKey jwtPublicKey;
        try {
            jwtPublicKey = utils.fromPemEncoded(constructedPublicKey);
        } catch (JoseException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, e.getMessage(), e);
            // If the public key is invalid then we cannot determine the tokens integrity.
            return false;
        }


        // Validate and process the JWT.
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // require the JWT to have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // require the subject claim
                .setExpectedIssuer(issuer) // whom the JWT needs to have been issued by
                .setExpectedAudience(audience) // to whom the JWT is intended for
                .setVerificationKey(jwtPublicKey) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, // which is only RS256 here
                                AlgorithmIdentifiers.RSA_USING_SHA256))
                .build(); // create the JwtConsumer instance

        try {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
            return true;
        } catch (InvalidJwtException e) {
            Log.e(TAG, "Invalid JWT provided", e);
            return false;
        }
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
            if (this.integrityCheckParameters != null) {
                jsonCredential.put("integrityCheck", this.integrityCheckParameters.serialize());
            }
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
        try {
            final JSONObject jsonCredential = new JSONObject(serializedCredential);
            final String serializedAuthState = jsonCredential.getString("authState");
            final String serializedIntegrityChecks = jsonCredential.getString("integrityCheck");
            final IntegrityCheckParameters icParams = IntegrityCheckParameters.deserialize(serializedIntegrityChecks);
            return new OIDCCredentials(serializedAuthState, icParams);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
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
     * @throws IllegalStateException
     */
    public boolean renew() throws AuthenticationException {
        throw new IllegalStateException("Not yet implemented");
    }
}
