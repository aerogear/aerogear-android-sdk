package org.aerogear.mobile.security.metrics;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SecurityCheckResultMetricTest {

    SecurityCheckResult result;
    final static String RESULT_NAME = "exampleName";
    final static boolean RESULT_PASSED = true;

    @Before
    public void setup() {
        result = new SecurityCheckResultImpl(RESULT_NAME, RESULT_PASSED);
    }

    @Test
    public void testConversion() {
        SecurityCheckResultMetric metric = new SecurityCheckResultMetric(result);
        assertEquals(RESULT_NAME, metric.identifier());
        assertEquals(String.valueOf(RESULT_PASSED), metric.data().get("passed"));
    }
}
