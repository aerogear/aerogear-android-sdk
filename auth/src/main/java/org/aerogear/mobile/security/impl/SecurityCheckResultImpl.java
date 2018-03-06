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

    private final String name;
    private final boolean passed;

    /**
     * Builds a new Security Check Result object.
     *
     * @param check the check class that produced this result
     * @param passed whether the check has been passed or not
     * @throws IllegalArgumentException if check is null
     */
    public SecurityCheckResultImpl(@NonNull SecurityCheck check, final boolean passed) {
        this.name = nonNull(check, "check").getName();
        this.passed = passed;
    }

    /**
     * Gets the name of the check.
     *
     * @return {@link String} name of check
     */
    @Override
    public String getName() {
        return name;
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
