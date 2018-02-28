package org.aerogear.mobile.security.checks;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * A check for whether the device the application is running on has a screen lock.
 */
public class ScreenLockCheck extends AbstractSecurityCheck {

    /**
     * Check whether the device has a screen lock enabled (PIN, Password, etc).
     *
     * @param context {@link Context} to be used by the check
     * @return <code>true</code> if the device has a screen lock enabled
     * @throws IllegalArgumentException if context is null
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        final KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        // KeyguardManager#isDeviceSecure() was added in Android M.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return keyguardManager.isDeviceSecure();
        }
        return keyguardManager.isKeyguardSecure();
    }
}
