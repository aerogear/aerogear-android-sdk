package org.aerogear.mobile.core.metrics;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MetricsServiceTest {

    @Test
    public void type() {
        MetricsService metricsService = new MetricsService();
        assertEquals("metrics", metricsService.type());
    }

    @Test
    public void defaultPublisherWithoutConfigUrl() {
        MobileCore mobileCore = MobileCore.init(RuntimeEnvironment.application);
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration.Builder().build();

        MetricsService metricsService = new MetricsService();
        metricsService.configure(mobileCore, serviceConfiguration);

        assertEquals(LoggerMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test
    public void defaultPublisherWithConfigUrl() {
        MobileCore mobileCore = MobileCore.init(RuntimeEnvironment.application);
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration.Builder()
            .setUrl("http://dummy.url")
            .build();

        MetricsService metricsService = new MetricsService();
        metricsService.configure(mobileCore, serviceConfiguration);

        assertEquals(NetworkMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test(expected = IllegalStateException.class)
    public void sendingDefaultMetricsWithoutConfigureService() {
        MetricsService metricsService = new MetricsService();
        metricsService.sendAppAndDeviceMetrics();
    }

    @Test(expected = IllegalStateException.class)
    public void sendingMetricsWithoutConfigureService() {
        MetricsService metricsService = new MetricsService();
        metricsService.publish(new DummyMetrics());
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
