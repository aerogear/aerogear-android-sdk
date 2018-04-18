package org.aerogear.mobile.core.unit.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;

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
    public void type() {
        MetricsService metricsService = new MetricsService();
        assertEquals("metrics", metricsService.type());
    }

    @Test
    public void defaultPublisherWithoutConfigUrl() {
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration.Builder().build();

        metricsService.configure(MobileCore.getInstance(), serviceConfiguration);

        assertEquals(LoggerMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test
    public void defaultPublisherWithConfigUrl() {
        ServiceConfiguration serviceConfiguration =
                        new ServiceConfiguration.Builder().setUrl("http://dummy.url").build();

        metricsService.configure(MobileCore.getInstance(), serviceConfiguration);

        assertEquals(NetworkMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test(expected = IllegalStateException.class)
    public void sendingDefaultMetricsWithoutConfigureService() {
        metricsService.sendAppAndDeviceMetrics(null);
    }

    @Test(expected = IllegalStateException.class)
    public void sendingMetricsWithoutConfigureService() {
        metricsService.publish("init", new DummyMetrics());
    }

    @Test
    public void testCallbackSuccessMethodIsCalled() {
        metricsService.configure(MobileCore.getInstance(),
                        new ServiceConfiguration.Builder().build());

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

        metricsService.publish("init", new DummyMetrics[] {new DummyMetrics()}, testCallback);
    }

    @Test
    public void testCallbackErrorMethodIsCalled() {
        ServiceConfiguration serviceConfiguration =
                        new ServiceConfiguration.Builder().setUrl("http://dummy").build();
        metricsService.configure(MobileCore.getInstance(), serviceConfiguration);

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
        metricsService.publish("init", new DummyMetrics[] {new DummyMetrics()}, testCallback);
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
