package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(mockSecurityCheck.test(any(Context.class)))
            .thenReturn(result);
        when(securityCheckType.getSecurityCheck())
            .thenReturn(mockSecurityCheck);
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

        Map<String, SecurityCheckResult> results = SecurityCheckExecutor.Builder
            .newSyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getName()));
        assertEquals(true, results.get(mockSecurityCheck.getName()).passed());
    }

    @Test
    public void testExecuteAsync() throws Exception {

        Map<String, Future<SecurityCheckResult>> results = SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withExecutorService(Executors.newFixedThreadPool(1))
            .build().execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getName()));
        assertEquals(true, results.get(mockSecurityCheck.getName()).get().passed());
    }

    @Test
    public void testSendMetricsAsync() throws Exception {
        when(metricsService.publish(any())).thenReturn(null);

        Map<String, Future<SecurityCheckResult>> results = SecurityCheckExecutor.Builder
            .newAsyncExecutor(context)
            .withSecurityCheck(securityCheckType)
            .withMetricsService(metricsService)
            .withExecutorService(Executors.newFixedThreadPool(1))
            .build()
            .execute();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(mockSecurityCheck.getName()));
        results.get(mockSecurityCheck.getName()).get();

        verify(metricsService, times(1)).publish(any());
    }
}
