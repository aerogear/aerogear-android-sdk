package org.aerogear.mobile.security.checks;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.content.Context;
import android.support.annotation.NonNull;

import org.aerogear.mobile.security.DeviceCheck;
import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.impl.DeviceCheckResultImpl;

/**
 * Base class for security checks.
 */
public abstract class AbstractDeviceCheck implements DeviceCheck {
    /**
     * Checks that the context is not null and delegates the check execution to the
     * {@link #execute(Context)} method.
     *
     * @param context Context to be used by the check
     * @return {@link DeviceCheckResult} embedding the result of {@link #execute(Context)}
     * @throws IllegalArgumentException if context is null
     */
    @Override
    public final DeviceCheckResult test(@NonNull final Context context) {
        return new DeviceCheckResultImpl(this, execute(nonNull(context, "context")));
    }

    /**
     * Executes the check. Context is guaranteed to be non null.
     *
     * @param context context to be used to perform the check
     * @return <code>true</code> if the check has passed, <code>false</code> otherwise. i.e. A
     *         <code>true</code> value implies a more secure environment for the application
     */
    protected abstract boolean execute(@NonNull final Context context);
}
