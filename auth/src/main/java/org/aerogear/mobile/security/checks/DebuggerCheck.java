package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.os.Debug;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;


/**
 * A check for whether a debugger is attached to the current application.
 */
public class DebuggerCheck implements SecurityCheck {
    private static final String NAME = "detectDebugger";

    /**
     * Check whether a debugger is attached to the current application.
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if device is in debug mode
     */
    @Override
    public SecurityCheckResult test(final Context context) {
        return new SecurityCheckResultImpl(NAME, Debug.isDebuggerConnected());
    }
}
