package org.aerogear.mobile.security;

import org.aerogear.mobile.security.checks.DeveloperModeCheck;
import org.aerogear.mobile.security.checks.RootedCheck;

/**
 * Checks that can be performed.
 */
public enum SecurityCheckType {
    /**
     *  Detect whether the device is rooted.
     *  See {@link RootedCheck}
     */
    IS_ROOTED(new RootedCheck()),
    /**
     * Detect if developer mode is enabled in the device.
     *  See {@link DeveloperModeCheck}
     */
    IS_DEVELOPER_MODE(new DeveloperModeCheck());

    private SecurityCheck check;

    SecurityCheckType(SecurityCheck check) {
        this.check = check;
    }

    /**
     * Return the {@link SecurityCheck} implementation for this check.
     *
     * @return a SecurityCheck instance
     */
    public SecurityCheck getSecurityCheck() {
        return check;
    }

    /**
     * Returns the name of this security check.
     * The value is the same as {@link SecurityCheck#getName()}
     * @return a String version of the name property.
     */
    public String getName() {
        return getSecurityCheck().getName();
    }
}
