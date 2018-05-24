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
public class DeveloperModeDisabledCheckTest {
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

        DeveloperModeDisabledCheck developerModeDisabledCheck = new DeveloperModeDisabledCheck();

        SecurityCheckResult expected =
                        new SecurityCheckResultImpl(developerModeDisabledCheck, false);
        SecurityCheckResult actual = developerModeDisabledCheck.test(context);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test
    public void developerModeDisabledTest() {
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                        0);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        DeveloperModeDisabledCheck developerModeDisabledCheck = new DeveloperModeDisabledCheck();

        SecurityCheckResult expected =
                        new SecurityCheckResultImpl(developerModeDisabledCheck, true);
        SecurityCheckResult actual = developerModeDisabledCheck.test(context);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        DeveloperModeDisabledCheck developerModeDisabledCheck = new DeveloperModeDisabledCheck();
        developerModeDisabledCheck.test(null);
    }
}
