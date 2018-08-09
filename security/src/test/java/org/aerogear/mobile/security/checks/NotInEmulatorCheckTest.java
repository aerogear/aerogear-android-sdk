package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.test.mock.MockContext;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class NotInEmulatorCheckTest {

    NotInEmulatorCheck check;

    @Before
    public void setup() {
        check = new NotInEmulatorCheck();
    }

    @Test
    public void testCheck() {
        DeviceCheckResult result = check.test(new MockContext());
        assertTrue(result.passed());
    }

}
