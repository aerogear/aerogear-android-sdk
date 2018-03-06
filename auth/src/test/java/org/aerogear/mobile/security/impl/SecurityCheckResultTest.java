package org.aerogear.mobile.security.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;


public class SecurityCheckResultTest {

    SecurityCheckResult result;
    static final boolean RESULT_PASSED = true;

    @Mock
    private SecurityCheck securityCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        result = new SecurityCheckResultImpl(securityCheck, RESULT_PASSED);
    }

    @Test
    public void testGetName() {
        assertEquals(securityCheck.getType(), result.getType());
    }

    @Test
    public void testPassed() {
        assertTrue(result.passed());
    }
}
