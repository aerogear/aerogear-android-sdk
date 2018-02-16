package org.aerogear.mobile.core.metrics;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class DefaultMetricsTest {
    @Test
    public void testType() {
        Application context = RuntimeEnvironment.application;
        DefaultMetrics metrics = new DefaultMetrics(context);
        Map<String, String> result = metrics.getDefaultMetrics();
        assertNotNull(result.get("clientId"));
        assertNotNull(result.get("appId"));
        assertNotNull(result.get("appVersion"));
        assertNotNull(result.get("sdkVersion"));
        assertNotNull(result.get("platform"));
        assertNotNull(result.get("platformVersion"));
    }
}
