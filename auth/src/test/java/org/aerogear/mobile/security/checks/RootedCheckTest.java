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

import org.aerogear.mobile.security.SecurityCheckResult;

@RunWith(RobolectricTestRunner.class)
public class RootedCheckTest {

    RootedCheck check;

    @Mock
    RootBeer rootBeer;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        check = new RootedCheck() {
            @Override
            protected RootBeer getRootBeer(Context ctx) {
                return rootBeer;
            }
        };
    }

    @Test
    public void testIsRooted() {
        when(rootBeer.isRooted()).thenReturn(true);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertTrue(result.passed());
    }

    @Test
    public void testNotRooted() {
        when(rootBeer.isRooted()).thenReturn(false);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        RootedCheck rootedCheck = new RootedCheck();
        rootedCheck.test(null);
    }
}
