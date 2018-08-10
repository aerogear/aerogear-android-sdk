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

import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.impl.DeviceCheckResultImpl;

@RunWith(RobolectricTestRunner.class)
public class DeveloperModeEnabledCheckTest {
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

        DeveloperModeEnabledCheck developerModeEnabledCheck = new DeveloperModeEnabledCheck();

        DeviceCheckResult expected = new DeviceCheckResultImpl(developerModeEnabledCheck, true);
        DeviceCheckResult actual = developerModeEnabledCheck.test(context);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test
    public void developerModeDisabledTest() {
        Settings.Global.putInt(mockContentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                        0);
        when(context.getContentResolver()).thenReturn(mockContentResolver);

        DeveloperModeEnabledCheck developerModeEnabledCheck = new DeveloperModeEnabledCheck();

        DeviceCheckResult expected = new DeviceCheckResultImpl(developerModeEnabledCheck, false);
        DeviceCheckResult actual = developerModeEnabledCheck.test(context);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.passed(), actual.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        DeveloperModeEnabledCheck developerModeEnabledCheck = new DeveloperModeEnabledCheck();
        developerModeEnabledCheck.test(null);
    }
}
