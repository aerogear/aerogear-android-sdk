package org.aerogear.mobile.security;

import android.content.Context;

/**
 * Interface for a single check to be executed.
 * <p>
 * An example of a check would be detecting whether the device an application is running on is
 * rooted.
 */
public interface SecurityCheck {
    /**
     * Perform the check and return a result
     *
     * @param context Context to be used by the check.
     * @return {@link SecurityCheckResult Result} of the test
     */
    SecurityCheckResult test(Context context);

    /**
     * Gets the name of the check. It must be a unique string.
     * The default implementation is to return the check class name.
     *
     * @return the name of the check.
     */
    default public String getName() {
        return this.getClass().getName();
    }
}
