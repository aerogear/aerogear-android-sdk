package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.os.Debug;
import android.support.annotation.NonNull;

/**
 * A check for whether a debugger is attached to the current application.
 */
public class DebuggerCheck extends AbstractSecurityCheck {

    /**
     * Check whether a debugger is attached to the current application. An application running with
     * an attached debugger can have its internals exposed.
     *
     * @param context {@link Context} to be used by the check.
     * @return <code>true</code> if device is not in debug mode
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        return !Debug.isDebuggerConnected();
    }

    @Override
    public String getName() {
        return "Debugger Check";
    }
}
