package org.aerogear.mobile.security;

import android.content.Context;

public interface SecurityCheck {
    /**
     * Perform the check and return a result
     *
     * @param context Context to be used by the check.
     * @return {@link SecurityCheckResult Result} of the test
     */
    SecurityCheckResult test(Context context);
}
