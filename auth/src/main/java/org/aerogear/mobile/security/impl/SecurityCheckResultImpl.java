package org.aerogear.mobile.security.impl;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;

/**
 * Implementation of {@link SecurityCheckResult}. A basic container for information about the
 * outcome of the check.
 */
public class SecurityCheckResultImpl implements SecurityCheckResult {

    private final String type;
    private final boolean passed;
    private final String displayName;

    /**
     * Builds a new Security Check Result object.
     *
     * @param check the check class that produced this result
     * @param passed whether the check has been passed or not
     * @throws IllegalArgumentException if check is null
     */
    public SecurityCheckResultImpl(@NonNull SecurityCheck check, final boolean passed) {
        SecurityCheck nonNullCheck = nonNull(check, "check");
        this.type = nonNullCheck.getType();
        this.displayName = nonNullCheck.getDisplayName();
        this.passed = passed;
    }

    /**
     * Gets the type of the check.
     *
     * @return {@link String} type of check
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets the Display Name of the check.
     *
     * @return {@link String} Display Name of check
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the check result.
     *
     * @return <code>true</code> if the check was successful
     */
    @Override
    public boolean passed() {
        return passed;
    }
}
