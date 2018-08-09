package org.aerogear.mobile.security.checks;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.RuntimeEnvironment.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.aerogear.mobile.security.DeviceCheckResult;

@RunWith(RobolectricTestRunner.class)
public class BackupDisallowedCheckTest {

    BackupDisallowedCheck check;
    PackageInfo packageInfo;

    @Before
    public void setup() throws PackageManager.NameNotFoundException {
        check = new BackupDisallowedCheck();
        packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(),
                        0);
    }

    @Test
    public void checkIsEnabled() throws PackageManager.NameNotFoundException {
        // Set the allow backup flag
        packageInfo.applicationInfo.flags =
                        packageInfo.applicationInfo.flags | ApplicationInfo.FLAG_ALLOW_BACKUP;

        DeviceCheckResult result = check.test(application);
        assertFalse(result.passed());
    }

    @Test
    public void checkNotEnabled() throws PackageManager.NameNotFoundException {
        // Unset the allow backup flag
        packageInfo.applicationInfo.flags =
                        packageInfo.applicationInfo.flags & ~ApplicationInfo.FLAG_ALLOW_BACKUP;

        DeviceCheckResult result = check.test(application);
        assertTrue(result.passed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContextTest() {
        check.test(null);
    }
}
