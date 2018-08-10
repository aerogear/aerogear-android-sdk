package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import com.scottyab.rootbeer.RootBeer;

import android.content.Context;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class RootEnabledCheckTest {

    RootEnabledCheck check;

    @Mock
    RootBeer rootBeer;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        check = new RootEnabledCheck() {
            @Override
            protected RootBeer getRootBeer(Context ctx) {
                return rootBeer;
            }
        };
    }

    @Test
    public void testIsRooted() {
        when(rootBeer.isRooted()).thenReturn(true);
        DeviceCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    @Test
    public void testNotRooted() {
        when(rootBeer.isRooted()).thenReturn(false);
        DeviceCheckResult result = check.test(RuntimeEnvironment.application);
        assertTrue(result.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        RootEnabledCheck rootEnabledCheck = new RootEnabledCheck();
        rootEnabledCheck.test(null);
    }
}
