package org.aerogear.mobile.security.checks;

import android.content.Context;

import com.scottyab.rootbeer.RootBeer;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

public class RootedCheck implements SecurityCheck {
    private static final String NAME = "detectRooted";

    /**
     * Check whether the device is rooted or not.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the device is rooted.
     */
    @Override
    public SecurityCheckResult test(Context context) {
        RootBeer rootBeer = new RootBeer(context);
        return new SecurityCheckResultImpl(NAME, rootBeer.isRooted());
    }
}
