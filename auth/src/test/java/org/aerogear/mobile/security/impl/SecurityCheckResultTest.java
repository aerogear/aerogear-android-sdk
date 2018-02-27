package org.aerogear.mobile.security.impl;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class SecurityCheckResultTest {

    static final boolean RESULT_PASSED = true;
    SecurityCheckResult result;
    @Mock
    private SecurityCheck securityCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        result = new SecurityCheckResultImpl(securityCheck, RESULT_PASSED);
    }

    @Test
    public void testGetName() {
        assertEquals(securityCheck.getName(), result.getName());
    }

    @Test
    public void testPassed() {
        assertTrue(result.passed());
    }
}
