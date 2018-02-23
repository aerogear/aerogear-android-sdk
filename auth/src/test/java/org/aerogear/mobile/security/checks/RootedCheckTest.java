package org.aerogear.mobile.security.checks;

import com.scottyab.rootbeer.RootBeer;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RootedCheckTest {

    RootedCheck check;

    @Mock
    RootBeer rootBeer;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        check = new RootedCheck();
    }

    @Test
    public void testIsRooted() {
        when(rootBeer.isRooted()).thenReturn(true);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application, rootBeer);
        assertTrue(result.passed());
    }

    @Test
    public void testNotRooted() {
        when(rootBeer.isRooted()).thenReturn(false);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application, rootBeer);
        assertFalse(result.passed());
    }
}
