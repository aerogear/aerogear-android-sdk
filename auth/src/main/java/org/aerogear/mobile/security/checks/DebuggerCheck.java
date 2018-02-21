package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.os.Debug;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;


/**
 * A check for whether the device the application is running on is in debugger mode.
 */
public class DebuggerCheck implements SecurityCheck {
    private static final String NAME = "detectDebugger";

    /**
     * Check whether the device is in debug mode
     * @param context Context to be used by the check.
     * @return <code>true</code> if device is in debug mode
     */
    @Override
    public SecurityCheckResult test(Context context) {
        return new SecurityCheckResultImpl(NAME, Debug.isDebuggerConnected());
    }
}
