package org.aerogear.mobile.security.impl;

import org.aerogear.mobile.security.SecurityCheckResult;

/**
 * Implementation of {@link SecurityCheckResult}. A basic container for information about the
 * outcome of the check.
 */
public class SecurityCheckResultImpl implements SecurityCheckResult {

    private final String name;
    private final boolean passed;

    public SecurityCheckResultImpl(final String name, final boolean passed) {
        this.name = name;
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
