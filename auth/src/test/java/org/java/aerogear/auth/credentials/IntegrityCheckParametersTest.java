package org.java.aerogear.auth.credentials;

import org.aerogear.auth.credentials.IntegrityCheckParameters;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class IntegrityCheckParametersTest {

    private static final String INTEGRITY_CHECK_ISSUER = "testIssuer";
    private static final String INTEGRITY_CHECK_AUDIENCE = "testAudience";

    private String testSerializedParams = "{\"audience\":\"" + INTEGRITY_CHECK_AUDIENCE +
        "\",\"issuer\":\"" + INTEGRITY_CHECK_ISSUER + "\"}";
    private IntegrityCheckParameters testParams;

    @Before
    public void setup() {
        this.testParams = new IntegrityCheckParameters(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER);
    }

    @Test
    public void testSerialize() throws JSONException {
        JSONObject jsonParam = new JSONObject(testParams.serialize());
        assertEquals(jsonParam.toString(), testSerializedParams);
    }

    @Test
    public void testDeserialize() throws JSONException {
        IntegrityCheckParameters checkParams = IntegrityCheckParameters.deserialize(testSerializedParams);
        assertEquals(checkParams.getAudience(), INTEGRITY_CHECK_AUDIENCE);
        assertEquals(checkParams.getIssuer(), INTEGRITY_CHECK_ISSUER);
    }

    @Test
    public void testIsValidSuccess() {
        IntegrityCheckParameters checkParams = new IntegrityCheckParameters(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER);
        assertTrue(checkParams.isValid());
    }

    @Test
    public void testIsValidFailure() {
        IntegrityCheckParameters checkParams = new IntegrityCheckParameters(null, null);
        assertFalse(checkParams.isValid());
    }
}
