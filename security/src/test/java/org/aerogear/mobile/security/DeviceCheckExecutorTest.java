package org.aerogear.mobile.security;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Requester;
import org.aerogear.mobile.security.impl.DeviceCheckResultImpl;

@RunWith(RobolectricTestRunner.class)
public class DeviceCheckExecutorTest {
    @Mock
    Context context;

    @Mock
    DeviceCheckType deviceCheckType;

    @Mock
    MetricsService metricsService;

    @Mock
    DeviceCheck mockDeviceCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        DeviceCheckResultImpl result = new DeviceCheckResultImpl(mockDeviceCheck, true);
        when(metricsService.publish(any(), any())).thenReturn(Requester.emit(true));
        when(context.getApplicationContext()).thenReturn(context);
        when(mockDeviceCheck.test(context)).thenReturn(result);
        when(deviceCheckType.getDeviceCheck()).thenReturn(mockDeviceCheck);
    }

    @Test
    public void testSendMetricsSync() {
        DeviceCheckExecutor.Builder.newSyncExecutor(context).withSecurityCheck(deviceCheckType)
                        .withMetricsService(metricsService).build().execute();

        verify(metricsService, times(1)).publish(eq("security"), any());
    }

    @Test
    public void testExecuteSync() {

        Map<String, DeviceCheckResult> results =
                        DeviceCheckExecutor.Builder.newSyncExecutor(context)
                                        .withSecurityCheck(deviceCheckType).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockDeviceCheck.getId()));
        assertEquals(true, results.get(mockDeviceCheck.getId()).passed());
    }

    @Test
    public void testExecuteAsync() throws Exception {

        final Map<String, Future<DeviceCheckResult>> results = DeviceCheckExecutor.Builder
                        .newAsyncExecutor(context).withSecurityCheck(deviceCheckType)
                        .withExecutorService(Executors.newFixedThreadPool(1)).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockDeviceCheck.getId()));
        assertEquals(true, results.get(mockDeviceCheck.getId()).get().passed());
    }

    @Test
    public void testSendMetricsAsync() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        final Map<String, Future<DeviceCheckResult>> results = DeviceCheckExecutor.Builder
                        .newAsyncExecutor(context).withSecurityCheck(deviceCheckType)
                        .withMetricsService(metricsService)
                        .withExecutorService(Executors.newFixedThreadPool(1)).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockDeviceCheck.getId()));
        results.get(mockDeviceCheck.getId()).get();

        ExecutorService executorService = (new AppExecutors()).networkThread();
        executorService.submit(() -> {
            try {
                verify(metricsService, times(1)).publish(eq("security"), any());
            } finally {
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);

    }
}
