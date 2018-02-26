package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.os.Build;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

/**
 * A check for whether the device the application is running on an emulator
 */
public class EmulatorCheck implements SecurityCheck {
    private static final String NAME = "emulatorCheck";

    /**
     * Check whether the device is an emulator.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the device is an emulator.
     */
    @Override
    public SecurityCheckResult test(final Context context) {
        return new SecurityCheckResultImpl(NAME, isEmulator());
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Checks if device is an emulator by looking at the following:
     * Fingerprint starts with 'generic' or 'unknown'
     * The model contains 'google_sdk' or 'emulator' or 'android sdk built for x86'
     * If the serial is equal to 'null'
     * The manufacturer contains 'genymotion'
     * If the brand and device start with 'generic'
     *
     * @return <code>true</code> if device is an emulator
     */
    private boolean isEmulator(){
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.SERIAL == null
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk".equals(Build.PRODUCT);
    }
}
