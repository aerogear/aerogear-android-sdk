package org.aerogear.mobile.core.integration;

import static org.junit.Assert.assertNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.categories.IntegrationTest;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.core.unit.metrics.MetricsServiceTest;

@RunWith(AeroGearTestRunner.class)
@SmallTest
@Category(IntegrationTest.class)
public class MetricsServiceIntegrationTest {

    private MetricsService metricsService;
    private Throwable error;

    @Before
    public void setUp() {
        MobileCore.init(RuntimeEnvironment.application);
        metricsService = MobileCore.getInstance().getMetricsService();

        error = null;
    }

    @Test
    public void testSendDefaultMetricsShouldReturnNoError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        metricsService.sendAppAndDeviceMetrics().requestOn(new AppExecutors().mainThread())
                        .respondWith(new Responder<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                error = exception;
                                latch.countDown();
                            }
                        });

        latch.await(10, TimeUnit.SECONDS);

        assertNull(error);
    }

    @Test
    public void testPublishMetricsShouldReturnNoError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Metrics metrics = new MetricsServiceTest.DummyMetrics();

        metricsService.publish("init", metrics).requestOn(new AppExecutors().mainThread())
                        .respondWith(new Responder<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                error = exception;
                                latch.countDown();
                            }
                        });

        latch.await(10, TimeUnit.SECONDS);

        assertNull(error);
    }

}
