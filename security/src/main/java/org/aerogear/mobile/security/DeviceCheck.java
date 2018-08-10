package org.aerogear.mobile.security;

import android.content.Context;

/**
 * Interface for a single check to be executed.
 *
 * An example of a check would be detecting whether the device an application is running on is
 * rooted.
 */
public interface DeviceCheck {
    /**
     * Perform the check and return a result.
     *
     * @param context {@link Context} to be used by the check
     * @return {@link DeviceCheckResult} of the test
     */
    DeviceCheckResult test(Context context);

    /**
     * Gets the name of the check to be used for display purposes in reports. This value should
     * match other checks that implement the same verification across different platforms (i.e. iOS,
     * cordova)
     *
     * @return {@link String} name of the check
     */
    String getName();

    /**
     * Gets the type of the check. It must be a unique string. The default implementation is to
     * return the check class name.
     *
     * @return {@link String} name of the check
     */
    default String getId() {
        return this.getClass().getName();
    }
}
