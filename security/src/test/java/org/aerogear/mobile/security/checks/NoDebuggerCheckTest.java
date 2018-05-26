package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import org.aerogear.mobile.security.SecurityCheckResult;

@RunWith(RobolectricTestRunner.class)
public class NoDebuggerCheckTest {
    NoDebuggerCheck check;

    @Before
    public void setup() {
        check = new NoDebuggerCheck();
    }

    @Test
    public void testDebugIsEnabled() {
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertTrue(result.passed());
    }
}
