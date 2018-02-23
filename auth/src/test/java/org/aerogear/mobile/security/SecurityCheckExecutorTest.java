package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.utils.MockSecurityCheck;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static junit.framework.Assert.assertEquals;

public class SecurityCheckExecutorTest {
    @Mock
    Context context;

    @Mock
    SecurityCheckType securityCheckType;

    @Mock
    MetricsService metricsService;

    SecurityCheck mockSecurityCheck;
//    SyncSecurityCheckExecutor executor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

  //      executor = new SyncSecurityCheckExecutor(context);
        mockSecurityCheck = new MockSecurityCheck();

        when(securityCheckType.getSecurityCheck()).thenReturn(mockSecurityCheck);
    }

    @Test
    public void testSendMetricsSync() {
        when(metricsService.publish(any())).thenReturn(null);

        SecurityCheckExecutor.Builder
            .newSyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withMetricsService(metricsService)
            .build().execute();

        verify(metricsService, times(1)).publish(any());

    }

    @Test
    public void testExecuteSync() {

        SecurityCheckResult[] results =  SecurityCheckExecutor.Builder
            .newSyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .build().execute();

        assertEquals(1, results.length);
        assertEquals(true, results[0].passed());
    }

    @Test
    public void testExecuteAsync() throws Exception {

        Future<SecurityCheckResult>[] results =  SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withExecutorService(Executors.newFixedThreadPool(1))
            .build().execute();

        assertEquals(1, results.length);
        assertEquals(true, results[0].get().passed());
    }

    @Test
    public void testSendMetricsAsync() throws Exception {
        when(metricsService.publish(any())).thenReturn(null);

        Future<SecurityCheckResult>[] results = SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withMetricsService(metricsService)
            .withExecutorService(Executors.newFixedThreadPool(1))
            .build().execute();

        results[0].get();

        verify(metricsService, times(1)).publish(any());
    }

    @Test
    public void testSendMetricsASyncCallback() throws Exception {
        when(metricsService.publish(any())).thenReturn(null);

        CountDownLatch cdl = new CountDownLatch(1);

        SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withMetricsService(metricsService)
            .withExecutorService(Executors.newFixedThreadPool(1))
            .build()
            .execute(new Callback() {
                @Override
                public void onSuccess(SecurityCheckResult models) {

                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }

                @Override
                public void onComplete() {
                    cdl.countDown();
                }
            });

        cdl.await(1000, TimeUnit.MILLISECONDS);

        verify(metricsService, times(1)).publish(any());
    }
}
