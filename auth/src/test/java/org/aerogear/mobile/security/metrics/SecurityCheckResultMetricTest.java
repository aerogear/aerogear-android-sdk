package org.aerogear.mobile.security.metrics;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SecurityCheckResultMetricTest {

    SecurityCheckResult result;
    final static boolean RESULT_PASSED = true;

    @Mock
    private SecurityCheck securityCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        result = new SecurityCheckResultImpl(securityCheck, RESULT_PASSED);
    }

    @Test
    public void testConversion() {
        SecurityCheckResultMetric metric = new SecurityCheckResultMetric(result);
        assertEquals(securityCheck.getName(), metric.identifier());
        assertEquals(String.valueOf(RESULT_PASSED), metric.data().get("passed"));
    }
}
