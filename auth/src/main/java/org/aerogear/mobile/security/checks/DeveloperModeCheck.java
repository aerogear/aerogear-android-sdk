package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;


/**
 * Security check that detects if developer mode is enabled in the device.
 */
public class DeveloperModeCheck implements SecurityCheck {
    /**
     * Check if developer mode has been enabled in the device.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the developer options have been enabled on the device.
     * @throws IllegalArgumentException if {@param context} is null
     */
    @Override
    public SecurityCheckResult test(@NonNull final Context context) {
        int devOptions = Settings.Secure.getInt(nonNull(context, "context").getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        return new SecurityCheckResultImpl(this, devOptions > 0);
    }
}
