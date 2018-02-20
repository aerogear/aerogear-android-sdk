package org.aerogear.mobile.security.impl;

import org.aerogear.mobile.security.SecurityCheckResult;

public class SecurityCheckResultImpl implements SecurityCheckResult {

    private final String name;
    private final boolean passed;

    public SecurityCheckResultImpl(String name, boolean passed) {
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
