package org.aerogear.mobile.core.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.RuntimeEnvironment;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.MetricsService;

@RunWith(JUnit4.class)
public class MetricsServiceIntegrationTest {

    MobileCore mobileCore;
    MetricsService metricsService;

    @Before
    public void setUp() throws Exception {
        mobileCore = MobileCore.init(RuntimeEnvironment.application);
        metricsService = new MetricsService();
    }

    @Test
    public void testSendMetricsShouldReturnNoError() throws Exception {
        ServiceConfiguration serviceConfiguration =
                        new ServiceConfiguration.Builder().setUrl("TODO put integration server URL").build();
        metricsService.configure(mobileCore, serviceConfiguration);

        final Callback testCallback = new Callback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(Throwable error) {
                fail("Should not throw an error: " + error.getMessage());
            }
        };

        metricsService.sendAppAndDeviceMetrics(testCallback);
    }

}
