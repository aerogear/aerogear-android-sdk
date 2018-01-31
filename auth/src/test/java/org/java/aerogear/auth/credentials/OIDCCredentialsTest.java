package org.java.aerogear.auth.credentials;

import net.openid.appauth.AuthState;

import org.aerogear.auth.credentials.IntegrityCheckParameters;
import org.aerogear.auth.credentials.OIDCCredentials;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class OIDCCredentialsTest {

    private static final String CREDENTIAL_AUTH_STATE = new AuthState().jsonSerializeString();
    private static final String INTEGRITY_CHECK_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8EyvaFPHT1fMrFmDK03oN8Q2anh+3e894cXBXazHzzaJD+Lz1HfOOZFeInkAasxWSo8KN1+Kg+1Z7QyrPLhfcbIwfH2Stabx+3lfEMtPGws7tqWg93piA8is1PwIV5/8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN++sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX+k6IWu68y/H6cicLXwS1VXPMwFjDOj7cQZB7A3t4q0F+6NVL+t7UjrAAK/7V3lPB+rDwHO92iwlZwIDAQAB";
    private static final String INTEGRITY_CHECK_ISSUER = "testIssuer";
    private static final String INTEGRITY_CHECK_AUDIENCE = "testAudience";

    private String testSerializedCredential = "{ \"authState\": {}, " +
        " \"integrityCheck\": { \"issuer\": \"" + INTEGRITY_CHECK_ISSUER +
        "\", \"audience\": \"" + INTEGRITY_CHECK_AUDIENCE +
        "\", \"publicKey\": \"" + INTEGRITY_CHECK_KEY + "\" } } ";
    private OIDCCredentials testCredential;

    @Before
    public void setup() throws JSONException {
        IntegrityCheckParameters checkParameters = new IntegrityCheckParameters(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER, INTEGRITY_CHECK_KEY);
        this.testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE, checkParameters);
    }

    @Test
    public void testSerialize() throws JSONException {
        JSONObject serializedCredential = new JSONObject(this.testCredential.serialize());
        assertEquals(serializedCredential.get("authState"), CREDENTIAL_AUTH_STATE);
        assertNotNull(serializedCredential.getString("integrityCheck"));
    }

    @Test
    public void testDeserialize() throws JSONException {
        OIDCCredentials credential = OIDCCredentials.deserialize(testSerializedCredential);
        assertEquals(credential.getIntegrityCheckParameters().getAudience(), INTEGRITY_CHECK_AUDIENCE);
        assertEquals(credential.getIntegrityCheckParameters().getIssuer(), INTEGRITY_CHECK_ISSUER);
        assertEquals(credential.getIntegrityCheckParameters().getPublicKey(), INTEGRITY_CHECK_KEY);
    }
}
