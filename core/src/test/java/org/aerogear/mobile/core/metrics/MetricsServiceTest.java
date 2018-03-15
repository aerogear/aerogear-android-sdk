package org.aerogear.mobile.core.metrics;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MetricsServiceTest {

    MobileCore mobileCore;
    MetricsService metricsService;

    @Before
    public void setUp() throws Exception {
        mobileCore = MobileCore.init(RuntimeEnvironment.application);
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

        metricsService.configure(mobileCore, serviceConfiguration);

        assertEquals(LoggerMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test
    public void defaultPublisherWithConfigUrl() {
        ServiceConfiguration serviceConfiguration =
                        new ServiceConfiguration.Builder().setUrl("http://dummy.url").build();

        metricsService.configure(mobileCore, serviceConfiguration);

        assertEquals(NetworkMetricsPublisher.class, metricsService.getPublisher().getClass());
    }

    @Test(expected = IllegalStateException.class)
    public void sendingDefaultMetricsWithoutConfigureService() {
        metricsService.sendAppAndDeviceMetrics(null);
    }

    @Test(expected = IllegalStateException.class)
    public void sendingMetricsWithoutConfigureService() {
        metricsService.publish(new DummyMetrics[] {new DummyMetrics()}, null);
    }

    @Test
    public void testListenerSuccessMethodIsCalled() throws Exception {
        metricsService.configure(mobileCore, new ServiceConfiguration.Builder().build());

        Callback callback = Mockito.mock(Callback.class);

        metricsService.sendAppAndDeviceMetrics(callback);
        verify(callback, times(1)).onSuccess();

        metricsService.publish(new DummyMetrics[] {new DummyMetrics()}, callback);
        verify(callback, times(2)).onSuccess();
    }

    @Test
    public void testListenerErrorMethodIsCalled() throws Exception {
        ServiceConfiguration serviceConfiguration =
                        new ServiceConfiguration.Builder().setUrl("http://dummy").build();
        metricsService.configure(mobileCore, serviceConfiguration);

        Callback callback = Mockito.mock(Callback.class);

        metricsService.sendAppAndDeviceMetrics(callback);
        verify(callback, times(1)).onError(any());

        metricsService.publish(new DummyMetrics[] {new DummyMetrics()}, callback);
        verify(callback, times(2)).onError(any());
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
