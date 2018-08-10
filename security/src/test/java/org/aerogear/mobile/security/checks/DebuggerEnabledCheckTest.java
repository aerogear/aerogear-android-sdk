package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class DebuggerEnabledCheckTest {
    DebuggerEnabledCheck check;

    @Before
    public void setup() {
        check = new DebuggerEnabledCheck();
    }

    @Test
    public void testDebugIsEnabled() {
        DeviceCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }
}
