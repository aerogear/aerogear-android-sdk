package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SyncSecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
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
    public void testExecuteASync() throws Exception {

        Future<SecurityCheckResult>[] results =  SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .build().execute();

        assertEquals(1, results.length);
        assertEquals(true, results[0].get().passed());
    }

    @Test
    public void testSendMetricsASync() throws Exception {
        when(metricsService.publish(any())).thenReturn(null);

        Future<SecurityCheckResult>[] results = SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withMetricsService(metricsService)
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
            .build()
            .execute(new Callback<SecurityCheckResult>() {
                @Override
                public void onSuccess(SecurityCheckResult models) {
                    cdl.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                    cdl.countDown();
                }
            });

        cdl.await(1000, TimeUnit.MILLISECONDS);

        verify(metricsService, times(1)).publish(any());

    }
}
