package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.support.annotation.NonNull;

import com.scottyab.rootbeer.RootBeer;

/**
 * A check for whether the device the application is running on is rooted.
 */
public class RootedCheck extends AbstractSecurityCheck {

    /**
     * Check whether the device is rooted or not.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the device is rooted.
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        return getRootBeer(context).isRooted();
    }

    /**
     * This method allows us to perform unit testing on the Rooted Check
     * @param context Context to be used
     * @return a RootBeer instance
     */
    protected RootBeer getRootBeer(final Context context) {
        return new RootBeer(context);
   }
}
