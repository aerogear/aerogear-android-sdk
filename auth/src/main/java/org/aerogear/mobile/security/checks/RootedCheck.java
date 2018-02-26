package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.scottyab.rootbeer.RootBeer;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

/**
 * A check for whether the device the application is running on is rooted.
 */
public class RootedCheck implements SecurityCheck {
    /**
     * Check whether the device is rooted or not.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the device is rooted.
     */
    @Override
    public SecurityCheckResult test(final Context context) {
        final RootBeer rootBeer = new RootBeer(context);
        return new SecurityCheckResultImpl(this, rootBeer.isRooted());
    }

    @VisibleForTesting
    SecurityCheckResult test(final Context context, final RootBeer rootBeer) {
        return new SecurityCheckResultImpl(this, rootBeer.isRooted());
    }
}
