package org.aerogear.mobile.security;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

@RunWith(RobolectricTestRunner.class)
public class SecurityCheckExecutorTest {
    @Mock
    Context context;

    @Mock
    SecurityCheckType securityCheckType;

    @Mock
    MetricsService metricsService;

    @Mock
    SecurityCheck mockSecurityCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        SecurityCheckResultImpl result = new SecurityCheckResultImpl(mockSecurityCheck, true);

        when(context.getApplicationContext()).thenReturn(context);
        when(mockSecurityCheck.test(context)).thenReturn(result);
        when(securityCheckType.getSecurityCheck()).thenReturn(mockSecurityCheck);
    }

    @Test
    public void testSendMetricsSync() {
        SecurityCheckExecutor.Builder.newSyncExecutor(context).withSecurityCheck(securityCheckType)
                        .withMetricsService(metricsService).build().execute();

        verify(metricsService, times(1)).publish(any());
    }

    @Test
    public void testExecuteSync() {

        Map<String, SecurityCheckResult> results =
                        SecurityCheckExecutor.Builder.newSyncExecutor(context)
                                        .withSecurityCheck(securityCheckType).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getId()));
        assertEquals(true, results.get(mockSecurityCheck.getId()).passed());
    }

    @Test
    public void testExecuteAsync() throws Exception {

        final Map<String, Future<SecurityCheckResult>> results = SecurityCheckExecutor.Builder
                        .newAsyncExecutor(context).withSecurityCheck(securityCheckType)
                        .withExecutorService(Executors.newFixedThreadPool(1)).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getId()));
        assertEquals(true, results.get(mockSecurityCheck.getId()).get().passed());
    }

    @Test
    public void testSendMetricsAsync() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        final Map<String, Future<SecurityCheckResult>> results = SecurityCheckExecutor.Builder
                        .newAsyncExecutor(context).withSecurityCheck(securityCheckType)
                        .withMetricsService(metricsService)
                        .withExecutorService(Executors.newFixedThreadPool(1)).build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getId()));
        results.get(mockSecurityCheck.getId()).get();

        ExecutorService executorService = (new AppExecutors()).networkThread();
        executorService.submit(() -> {
            try {
                verify(metricsService, times(1)).publish(any());
            } finally {
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);

    }
}
