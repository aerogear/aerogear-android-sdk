package org.aerogear.mobile.security.checks;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DebuggerCheckTest {
    DebuggerCheck check;

    @Before
    public void setup() {
        check = new DebuggerCheck();
    }

    @Test
    public void testDebugIsEnabled() {
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }
}
