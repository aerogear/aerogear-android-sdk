package org.aerogear.mobile.security;

import org.aerogear.mobile.security.checks.RootedCheck;

public enum Check {
    /**
     *  Detect whether the device is rooted.
     *
     * @return <code>true</code> if the device is rooted.
     */
    IS_ROOTED(new RootedCheck());

    private SecurityCheck check;

    Check(SecurityCheck check) {
        this.check = check;
    }

    /**
     * Return the {@link SecurityCheck} implementation for this check.
     *
     * @return
     */
    public SecurityCheck getSecurityCheck() {
        return check;
    }
}
