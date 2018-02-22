package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.provider.Settings;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;


/**
 * Security check that detects if developer mode is enabled in the device.
 */
public class DeveloperModeCheck implements SecurityCheck {
    private static final String NAME = "detectDeveloperMode";

    /**
     * Check if developer mode has been enabled in the device.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the developer options have been enabled on the device.
     */
    @Override
    public SecurityCheckResult test(final Context context) {
        int devOptions = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        boolean devOptionsEnabled = devOptions > 0 ? true : false;
        return new SecurityCheckResultImpl(NAME, devOptionsEnabled);
    }
}
