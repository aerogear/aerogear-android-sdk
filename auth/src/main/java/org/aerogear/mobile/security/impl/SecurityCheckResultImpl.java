package org.aerogear.mobile.security.impl;

import android.support.annotation.NonNull;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Implementation of {@link SecurityCheckResult}. A basic container for information about the
 * outcome of the check.
 */
public class SecurityCheckResultImpl implements SecurityCheckResult {

    private final String name;
    private final boolean passed;

    /**
     * Builds a new Security Check result.
     * @param check the check class that produced this result
     * @param passed whether the check has been passed or not
     */
    public SecurityCheckResultImpl(@NonNull SecurityCheck check, final boolean passed) {
        this.name = nonNull(check, "check").getName();
        this.passed = passed;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean passed() {
        return passed;
    }
}
