package org.aerogear.mobile.security.impl;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.Check;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.utils.MockSecurityCheck;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static junit.framework.Assert.assertEquals;

public class SecurityCheckExecutorTest {
    @Mock
    Context context;

    @Mock
    Check check;

    @Mock
    MetricsService metricsService;

    SecurityCheck mockSecurityCheck;
    SecurityCheckExecutor executor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        executor = new SecurityCheckExecutorImpl(context);
        mockSecurityCheck = new MockSecurityCheck();

        when(check.getSecurityCheck()).thenReturn(mockSecurityCheck);
    }

    @Test
    public void testSendMetrics() {
        when(metricsService.publish(any())).thenReturn(null);

        executor.addCheck(check).addCheck(check).sendMetrics(metricsService).execute();

        verify(metricsService, times(2)).publish(any());

    }

    @Test
    public void testExecute() {
        SecurityCheckResult[] results = executor.addCheck(check).execute();
        assertEquals(1, results.length);
        assertEquals(true, results[0].passed());
    }
}
