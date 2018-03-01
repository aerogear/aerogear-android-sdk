package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * A check for whether the device the application is running on an emulator
 */
public class EmulatorCheck extends AbstractSecurityCheck {

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
    @Override
    protected boolean execute(@NonNull Context context) {
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
