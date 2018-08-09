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

import org.aerogear.mobile.security.DeviceCheck;
import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.impl.DeviceCheckResultImpl;

@RunWith(RobolectricTestRunner.class)
public class DeviceCheckResultMetricTest {

    DeviceCheckResult okResult;
    DeviceCheckResult failedResult;

    @Mock
    private DeviceCheck deviceCheck;
    private final String CHECK_ID = "TestCheck";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(deviceCheck.getId()).thenReturn(CHECK_ID);
        okResult = new DeviceCheckResultImpl(deviceCheck, true);
        failedResult = new DeviceCheckResultImpl(deviceCheck, false);
    }

    @Test
    public void testConversion() throws JSONException {
        SecurityCheckResultMetric metric = new SecurityCheckResultMetric(okResult, failedResult);
        assertEquals(SecurityCheckResultMetric.IDENTIFIER, metric.identifier());

        JSONArray data = metric.data();
        JSONObject okResultJson = data.getJSONObject(0);
        JSONObject failedResultJson = data.getJSONObject(1);

        assertEquals(CHECK_ID, okResultJson.get(SecurityCheckResultMetric.KEY_ID));
        assertEquals(true, okResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));

        assertEquals(CHECK_ID, failedResultJson.get(SecurityCheckResultMetric.KEY_ID));
        assertEquals(false, failedResultJson.getBoolean(SecurityCheckResultMetric.KEY_VALUE));
    }
}
