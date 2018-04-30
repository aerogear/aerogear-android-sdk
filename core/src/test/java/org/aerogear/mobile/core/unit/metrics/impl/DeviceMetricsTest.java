package org.aerogear.mobile.core.unit.metrics.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class DeviceMetricsTest {

    @Test
    public void testType() {
        DeviceMetrics deviceMetrics = new DeviceMetrics();
        assertEquals("device", deviceMetrics.getIdentifier());
    }

    @Test
    public void testData() throws JSONException {
        DeviceMetrics deviceMetrics = new DeviceMetrics();
        JSONObject result = deviceMetrics.getData();

        assertNotNull(result.getString("platform"));
        assertNotNull(result.getString("platformVersion"));
    }

}
