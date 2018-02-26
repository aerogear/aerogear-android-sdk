package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.provider.Settings;
import android.test.mock.MockContentResolver;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DeveloperModeCheckTest {
    @Mock
    Context context;

    MockContentResolver mockContentResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void developerModeEnabledTest() {
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        SecurityCheckResult expected = new SecurityCheckResultImpl("detectDeveloperMode", true);

        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();

        SecurityCheckResult actual = developerModeCheck.test(context);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test
    public void developerModeDisabledTest() {
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        SecurityCheckResult expected = new SecurityCheckResultImpl("detectDeveloperMode", false);

        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();

        SecurityCheckResult actual = developerModeCheck.test(context);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullContextTest() {
        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();
        developerModeCheck.test(null);
    }
}
