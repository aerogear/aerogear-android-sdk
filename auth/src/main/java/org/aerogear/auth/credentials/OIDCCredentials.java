package org.aerogear.auth.credentials;

import net.openid.appauth.AuthState;

import org.aerogear.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.lang.JoseException;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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
     * A function to verify the authenticity of a JWT token against a public key, expected issuer and audience.
     * @param jwtToken - The JWT Token to Verify.
     * @param publicKey - The Public Key from Keycloak, without the Begin/End tags.
     * @param issuer - The expected Issuer of the JWT
     * @param audience - The expected Audience of the JWT
     * @return boolean - true if the token integrity is good
     */
    public boolean verifyToken(String jwtToken, String publicKey, String issuer, String audience) {

        // TODO Get the Public Key from the Mobile Core Config
        // TODO Get the Audience from the Mobile Core Config
        // TODO Construct the Issuer using properties in the Mobile Core Config

        boolean verified = false;

        // add the Begin/End tags to the public key generated from Keycloak
        String beginPublicKey = "-----BEGIN PUBLIC KEY-----";
        String endPublicKey = "-----END PUBLIC KEY-----";
        String constructedPublicKey = beginPublicKey + publicKey + endPublicKey;

        // Convert the public key from a string to a Java security key
        RsaKeyUtil utils = new RsaKeyUtil();
        PublicKey jwtPublicKey = null;
        try {
            jwtPublicKey = utils.fromPemEncoded(constructedPublicKey);
        } catch (JoseException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
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

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
            verified = true;
            System.out.println("JWT Verified Successfully.");
        }
        catch (InvalidJwtException e)
        {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            System.out.println("JWT Validation Failed. " + e.getLocalizedMessage());

        }
        return verified;
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
