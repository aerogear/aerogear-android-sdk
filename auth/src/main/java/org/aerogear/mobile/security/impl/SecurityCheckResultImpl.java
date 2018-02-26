package org.aerogear.mobile.security.impl;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;

/**
 * Implementation of {@link SecurityCheckResult}. A basic container for information about the
 * outcome of the check.
 */
public class SecurityCheckResultImpl implements SecurityCheckResult {

    private final String name;
    private final boolean passed;

    public SecurityCheckResultImpl(SecurityCheck check, final boolean passed) {
        this.name = check.getName();
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
