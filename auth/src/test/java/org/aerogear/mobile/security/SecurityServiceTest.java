package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.utils.MockSecurityCheck;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecurityServiceTest {

    @Mock
    MobileCore mobileCore;

    @Mock
    Context context;

    @Mock
    MetricsService metricsService;

    @Mock
    Check check;

    SecurityService securityService;
    SecurityCheck securityCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mobileCore.getContext()).thenReturn(context);
        securityCheck = new MockSecurityCheck();
        when(check.getSecurityCheck()).thenReturn(securityCheck);

        securityService = new SecurityService();
        securityService.configure(mobileCore, null);
    }

    @Test
    public void testCheckAndSendMetric() {
        when(metricsService.publish()).thenReturn(null);

        securityService.checkAndSendMetric(check, metricsService);
        verify(metricsService, times(1)).publish(any());
    }
}
