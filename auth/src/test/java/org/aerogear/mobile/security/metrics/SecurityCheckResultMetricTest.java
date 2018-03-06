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
    private final String NAME = "TestCheck";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(securityCheck.getType()).thenReturn(NAME);
        okResult = new SecurityCheckResultImpl(securityCheck, true);
        failedResult = new SecurityCheckResultImpl(securityCheck, false);
    }

    @Test
    public void testConversion() throws JSONException {
        SecurityCheckResultMetric metric = new SecurityCheckResultMetric(okResult, failedResult);
        assertEquals(SecurityCheckResultMetric.IDENTIFIER, metric.identifier());

        JSONArray data = metric.data();
        JSONObject okResultJson = data.getJSONObject(0);
        JSONObject failedResultJson = data.getJSONObject(1);

        assertEquals(NAME, okResultJson.get(SecurityCheckResultMetric.KEY_TYPE));
        assertEquals(true, okResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));

        assertEquals(NAME, failedResultJson.get(SecurityCheckResultMetric.KEY_TYPE));
        assertEquals(false, failedResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));
    }
}
