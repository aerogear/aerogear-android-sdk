package org.aerogear.mobile.core.metrics.metrics;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.MobileCore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class AppMetricsTest {

    @Before
    public void setup() {
        MobileCore.init(RuntimeEnvironment.application);
    }

    @Test
    public void testType() {
        Application context = RuntimeEnvironment.application;

        AppMetrics appMetrics = new AppMetrics(context);
        assertEquals("app", appMetrics.identifier());
    }

    @Test
    public void testData() {
        Application context = RuntimeEnvironment.application;

        AppMetrics appMetrics = new AppMetrics(context);
        Map<String, String> result = appMetrics.data();

        assertNotNull(result.get("appId"));
        assertNotNull(result.get("appVersion"));
        assertNotNull(result.get("sdkVersion"));
    }

}
