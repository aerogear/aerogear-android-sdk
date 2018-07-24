package org.aerogear.mobile.core.unit.metrics;

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
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Responder;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MetricsServiceTest {

    private MetricsService metricsService;

    @Before
    public void setUp() {
        MobileCore.init(RuntimeEnvironment.application);
        metricsService = new MetricsService("http://dummy.io/metrics");
    }

    @Test
    public void testCallbackSuccessMethodIsCalled() {
        metricsService.publish("init", new DummyMetrics())
                        .requestOn(new AppExecutors().mainThread())
                        .respondWith(new Responder<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                assertTrue(true);
                            }

                            @Override
                            public void onException(Exception exception) {
                                fail("Shouldn't throw any error: " + exception.getMessage());
                            }
                        });
    }

    @Test
    public void testCallbackErrorMethodIsCalled() {
        metricsService.publish("init", new DummyMetrics())
                        .requestOn(new AppExecutors().mainThread())
                        .respondWith(new Responder<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                fail("Should throw an error");
                            }

                            @Override
                            public void onException(Exception exception) {
                                assertTrue(exception != null);

                            }
                        });
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
