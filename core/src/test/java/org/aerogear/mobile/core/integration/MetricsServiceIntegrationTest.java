package org.aerogear.mobile.core.integration;

import static org.junit.Assert.assertNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.unit.metrics.MetricsServiceTest;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MetricsServiceIntegrationTest {

    private static final String MOBILE_SERVICES_JSON = "integration-test-mobile-services.json";

    private MetricsService metricsService;
    private Throwable error;

    @Before
    public void setUp() throws Exception {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(MOBILE_SERVICES_JSON);

        MobileCore mobileCore = MobileCore.init(RuntimeEnvironment.application, options);
        metricsService = mobileCore.getInstance(MetricsService.class);

        error = null;
    }

    @Test
    public void testSendDefaultMetricsShouldReturnNoError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        final Callback testCallback = new Callback() {
            @Override
            public void onSuccess() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable err) {
                error = err;
                latch.countDown();
            }
        };

        metricsService.sendAppAndDeviceMetrics(testCallback);
        latch.await(10, TimeUnit.SECONDS);

        assertNull(error);
    }

    @Test
    public void testPublishMetricsShouldReturnNoError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Metrics metrics = new MetricsServiceTest.DummyMetrics();

        final Callback testCallback = new Callback() {
            @Override
            public void onSuccess() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable err) {
                error = err;
                latch.countDown();
            }
        };

        metricsService.publish("init", new Metrics[] {metrics}, testCallback);
        latch.await(10, TimeUnit.SECONDS);

        assertNull(error);
    }

}
