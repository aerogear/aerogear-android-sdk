package org.aerogear.mobile.security.checks;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

/**
 * A check for whether the device the application is running on has a screen lock.
 */
public class ScreenLockCheck implements SecurityCheck {
    private static final String NAME = "detectScreenLock";

    /**
     * Check whether the device has a screen lock enabled (PIN, Password, etc).
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the device has a screen lock enabled.
     */
    @Override
    public SecurityCheckResult test(final Context context){
        final KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        // KeyguardManager#isDeviceSecure() was added in Android M.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return new SecurityCheckResultImpl(NAME, keyguardManager.isDeviceSecure());
        }
        return new SecurityCheckResultImpl(NAME, keyguardManager.isKeyguardSecure());
    }
}
