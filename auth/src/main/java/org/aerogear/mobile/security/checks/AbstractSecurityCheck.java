package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.support.annotation.NonNull;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Base class for security checks.
 */
public abstract class AbstractSecurityCheck implements SecurityCheck {
    /**
     * Checks that the context is not null and delegates the check execution to the {@link #execute(Context)} method.
     *
     * @param context Context to be used by the check
     * @return {@link SecurityCheckResult} embedding the result of {@link #execute(Context)}
     * @throws IllegalArgumentException if context is null
     */
    @Override
    public final SecurityCheckResult test(@NonNull final Context context) {
        return new SecurityCheckResultImpl(this, execute(nonNull(context, "context")));
    }

    /**
     * Executes the check. Context is guaranteed to be non null.
     * @param context context to be used to perform the check
     * @return <code>true</code> if the check has passed, <code>false</code> otherwise
     */
    protected abstract boolean execute(@NonNull final Context context);
}
