package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertFalse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.os.Build;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class ScreenLockEnabledCheckTest {

    ScreenLockEnabledCheck check;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        check = new ScreenLockEnabledCheck();
    }

    @Test
    public void testAndroidM() throws NoSuchFieldException, Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.M);
        DeviceCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    @Test
    public void testPreAndroidM() throws NoSuchFieldException, Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.LOLLIPOP);
        DeviceCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        ScreenLockEnabledCheck screenLockEnabledCheck = new ScreenLockEnabledCheck();
        screenLockEnabledCheck.test(null);
    }
}
