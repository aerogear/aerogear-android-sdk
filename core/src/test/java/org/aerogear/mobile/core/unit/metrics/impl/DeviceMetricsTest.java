package org.aerogear.mobile.core.unit.metrics.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class DeviceMetricsTest {

    @Test
    public void testType() {
        Application context = RuntimeEnvironment.application;

        DeviceMetrics deviceMetrics = new DeviceMetrics(context);
        assertEquals("device", deviceMetrics.identifier());
    }

    @Test
    public void testData() throws JSONException {
        Application context = RuntimeEnvironment.application;

        DeviceMetrics deviceMetrics = new DeviceMetrics(context);
        JSONObject result = deviceMetrics.data();

        assertNotNull(result.getString("platform"));
        assertNotNull(result.getString("platformVersion"));
    }

}
