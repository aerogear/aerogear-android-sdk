package org.aerogear.mobile.core.unit.metrics;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MetricsServiceTest {

    private MetricsService metricsService;

    @Before
    public void setUp() {
        MobileCore.init(RuntimeEnvironment.application);
        metricsService = new MetricsService();
    }

    @Test
    public void testCallbackSuccessMethodIsCalled() {
        final Callback testCallback = new Callback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(Throwable error) {
                fail("Shouldn't throw any error: " + error.getMessage());
            }
        };
        metricsService.sendAppAndDeviceMetrics(testCallback);

        metricsService.publish("init", new DummyMetrics[]{new DummyMetrics()}, testCallback);
    }

    @Test
    public void testCallbackErrorMethodIsCalled() {
        final Callback testCallback = new Callback() {
            @Override
            public void onSuccess() {
                fail("Should throw an error");
            }

            @Override
            public void onError(Throwable error) {
                assertTrue(error != null);
            }
        };

        metricsService.sendAppAndDeviceMetrics(testCallback);
        metricsService.publish("init", new DummyMetrics[]{new DummyMetrics()}, testCallback);
    }

    public static class DummyMetrics implements Metrics<Map<String, String>> {

        @Override
        public String identifier() {
            return "dummy";
        }

        @Override
        public Map<String, String> data() {
            return new HashMap<>();
        }

    }

}
