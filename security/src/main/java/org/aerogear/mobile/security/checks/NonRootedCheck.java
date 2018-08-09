package org.aerogear.mobile.security.checks;

import com.scottyab.rootbeer.RootBeer;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * A check for whether the device the application is running on is rooted.
 */
public class NonRootedCheck extends AbstractDeviceCheck {

    /**
     * Check whether the device is rooted or not. An attacker running the application on a rooted
     * device can have direct access to its data storage and also has many more tools to eavesdrop
     * on networked communications
     *
     * @param context {@link Context} to be used by the check
     * @return <code>true</code> if the device is *not* rooted
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        return !getRootBeer(context).isRooted();
    }

    /**
     * This method allows us to perform unit testing on the Rooted Check
     *
     * @param context Context to be used
     * @return {@link RootBeer}
     */
    protected RootBeer getRootBeer(final Context context) {
        return new RootBeer(context);
    }

    @Override
    public String getName() {
        return "Rooted Check";
    }
}
