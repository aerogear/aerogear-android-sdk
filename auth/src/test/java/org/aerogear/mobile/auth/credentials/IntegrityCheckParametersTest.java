package org.aerogear.mobile.auth.credentials;

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

    private static final String INTEGRITY_CHECK_KEY = "testKey";
    private static final String INTEGRITY_CHECK_ISSUER = "testIssuer";
    private static final String INTEGRITY_CHECK_AUDIENCE = "testAudience";

    private String testSerializedParams = "{\"audience\":\"" + INTEGRITY_CHECK_AUDIENCE +
        "\",\"issuer\":\"" + INTEGRITY_CHECK_ISSUER + "\",\"publicKey\":\"" + INTEGRITY_CHECK_KEY + "\"}";
    private IntegrityCheckParametersImpl testParams;

    @Before
    public void setup() {
        this.testParams = new IntegrityCheckParametersImpl(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER, INTEGRITY_CHECK_KEY);
    }

    @Test
    public void testSerialize() throws JSONException {
        JSONObject jsonParam = new JSONObject(testParams.serialize());
        assertEquals(jsonParam.toString(), testSerializedParams);
    }

    @Test
    public void testDeserialize() throws JSONException {
        IntegrityCheckParametersImpl checkParams = IntegrityCheckParametersImpl.deserialize(testSerializedParams);
        assertEquals(checkParams.getAudience(), INTEGRITY_CHECK_AUDIENCE);
        assertEquals(checkParams.getIssuer(), INTEGRITY_CHECK_ISSUER);
    }

    @Test
    public void testIsValidSuccess() {
        IntegrityCheckParametersImpl checkParams = new IntegrityCheckParametersImpl(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER, INTEGRITY_CHECK_KEY);
        assertTrue(checkParams.isValid());
    }

    @Test
    public void testIsValidFailure() {
        IntegrityCheckParametersImpl checkParams = new IntegrityCheckParametersImpl();
        assertFalse(checkParams.isValid());
    }
}
