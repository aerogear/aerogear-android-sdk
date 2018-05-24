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

    private final String id;
    private final boolean passed;
    private final String name;

    /**
     * Builds a new Security Check Result object.
     *
     * @param check the check class that produced this result
     * @param passed whether the check has been passed or not
     * @throws IllegalArgumentException if check is null
     */
    public SecurityCheckResultImpl(@NonNull SecurityCheck check, final boolean passed) {
        SecurityCheck nonNullCheck = nonNull(check, "check");
        this.id = nonNullCheck.getId();
        this.name = nonNullCheck.getName();
        this.passed = passed;
    }

    /**
     * Gets the id of the check.
     *
     * @return {@link String} id of check
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Gets the Display Name of the check.
     *
     * @return {@link String} Display Name of check
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
