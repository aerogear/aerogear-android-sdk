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
        return new SecurityCheckResultImpl(this, getRootBeer(context).isRooted());
    }

   protected RootBeer getRootBeer(final Context ctx) {
        return new RootBeer(ctx);
   }
}
