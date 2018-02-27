package org.aerogear.mobile.security;


import org.aerogear.mobile.security.checks.DebuggerCheck;
import org.aerogear.mobile.security.checks.DeveloperModeCheck;
import org.aerogear.mobile.security.checks.EmulatorCheck;
import org.aerogear.mobile.security.checks.RootedCheck;
import org.aerogear.mobile.security.checks.ScreenLockCheck;

/**
 * Checks that can be performed.
 */
public enum SecurityCheckType {
    /**
     * Detect whether the device is rooted.
     * See {@link RootedCheck}
     */
    IS_ROOTED(new RootedCheck()),
    /**
     * Detect if developer mode is enabled in the device.
     * See {@link DeveloperModeCheck}
     */
    IS_DEVELOPER_MODE(new DeveloperModeCheck()),
    /**
     * Detect if a device is in debug mode
     * See {@link DebuggerCheck}
     */

    IS_DEBUGGER(new DebuggerCheck()),
    /**
     * Detect whether the device is emulated.
     * See {@link EmulatorCheck}
     */
    IS_EMULATOR(new EmulatorCheck()),
    /**
     * Detect whether a screen lock is enabled (PIN, Password etc).
     * See {@link ScreenLockCheck}
     */
    SCREEN_LOCK_ENABLED(new ScreenLockCheck());

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
     *
     * @return a String version of the name property.
     */
    public String getName() {
        return getSecurityCheck().getName();
    }
}
