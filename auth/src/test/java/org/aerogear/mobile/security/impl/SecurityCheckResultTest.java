package org.aerogear.mobile.security.impl;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SecurityCheckResultTest {

    SecurityCheckResult result;
    static final String RESULT_NAME = "testResult";
    static final boolean RESULT_PASSED = true;

    @Before
    public void setup() {
        result = new SecurityCheckResultImpl(RESULT_NAME, RESULT_PASSED);
    }

    @Test
    public void testGetName() {
        assertEquals(RESULT_NAME, result.getName());
    }

    @Test
    public void testPassed() {
        assertTrue(result.passed());
    }
}
