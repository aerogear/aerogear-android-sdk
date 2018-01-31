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
    private static final String INTEGRITY_CHECK_ISSUER = "testIssuer";
    private static final String INTEGRITY_CHECK_AUDIENCE = "testAudience";

    private String testSerializedCredential = "{ \"authState\": {}, " +
        " \"integrityCheck\": { \"issuer\": \"" + INTEGRITY_CHECK_ISSUER +
        "\", \"audience\": \"" + INTEGRITY_CHECK_AUDIENCE + "\" } } ";
    private OIDCCredentials testCredential;

    @Before
    public void setup() throws JSONException {
        IntegrityCheckParameters checkParameters = new IntegrityCheckParameters(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER);
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
    }
}
