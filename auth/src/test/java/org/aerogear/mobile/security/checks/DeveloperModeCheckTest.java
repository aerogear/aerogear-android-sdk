package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.provider.Settings;
import android.test.mock.MockContentResolver;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

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
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                        1);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();

        SecurityCheckResult expected = new SecurityCheckResultImpl(developerModeCheck, true);
        SecurityCheckResult actual = developerModeCheck.test(context);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test
    public void developerModeDisabledTest() {
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                        0);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();

        SecurityCheckResult expected = new SecurityCheckResultImpl(developerModeCheck, false);
        SecurityCheckResult actual = developerModeCheck.test(context);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        DeveloperModeCheck developerModeCheck = new DeveloperModeCheck();
        developerModeCheck.test(null);
    }
}
