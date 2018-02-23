package org.aerogear.mobile.security.checks;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static junit.framework.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class ScreenLockCheckTest {

    ScreenLockCheck check;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        check = new ScreenLockCheck();
    }

    @Test
    public void testAndroidM() throws NoSuchFieldException, Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.M);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    @Test
    public void testPreAndroidM() throws NoSuchFieldException, Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.LOLLIPOP);
        SecurityCheckResult result = check.test(RuntimeEnvironment.application);
        assertFalse(result.passed());
    }

    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
