package org.aerogear.auth.credentials;

import net.openid.appauth.AuthState;

import org.aerogear.auth.AuthenticationException;
import org.json.JSONException;
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
        throw new IllegalStateException("Not yet implemented");
    }

    /**
     * Returns stringified JSON for the OIDCCredential.
     * @return Stringified JSON OIDCCredential
     */
    public String serialise() {
        return this.authState.jsonSerializeString();
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
