package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.test.mock.MockContext;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class IsEmulatorCheckTest {

    IsEmulatorCheck check;

    @Before
    public void setup() {
        check = new IsEmulatorCheck();
    }

    @Test
    public void testCheck() {
        DeviceCheckResult result = check.test(new MockContext());
        assertFalse(result.passed());
    }

}
