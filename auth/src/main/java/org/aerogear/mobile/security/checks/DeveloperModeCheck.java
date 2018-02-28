package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;


/**
 * Security check that detects if developer mode is enabled in the device.
 */
public class DeveloperModeCheck extends AbstractSecurityCheck {

    /**
     * Check if developer mode has been enabled in the device.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the developer options have been enabled on the device.
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        int devOptions = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        return devOptions > 0;
    }
}
