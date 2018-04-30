package org.aerogear.mobile.security.metrics;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

@RunWith(RobolectricTestRunner.class)
public class SecurityCheckResultMetricTest {

    SecurityCheckResult okResult;
    SecurityCheckResult failedResult;

    @Mock
    private SecurityCheck securityCheck;
    private final String CHECK_ID = "TestCheck";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(securityCheck.getId()).thenReturn(CHECK_ID);
        okResult = new SecurityCheckResultImpl(securityCheck, true);
        failedResult = new SecurityCheckResultImpl(securityCheck, false);
    }

    @Test
    public void testConversion() throws JSONException {
        SecurityCheckResultMetric metric = new SecurityCheckResultMetric(okResult, failedResult);
        assertEquals(SecurityCheckResultMetric.IDENTIFIER, metric.getIdentifier());

        JSONArray data = metric.getData();
        JSONObject okResultJson = data.getJSONObject(0);
        JSONObject failedResultJson = data.getJSONObject(1);

        assertEquals(CHECK_ID, okResultJson.get(SecurityCheckResultMetric.KEY_ID));
        assertEquals(true, okResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));

        assertEquals(CHECK_ID, failedResultJson.get(SecurityCheckResultMetric.KEY_ID));
        assertEquals(false, failedResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));
    }
}
