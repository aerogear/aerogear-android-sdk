package org.aerogear.mobile.security;


import org.aerogear.mobile.security.checks.AllowBackupFlagCheck;
import org.aerogear.mobile.security.checks.DeveloperModeCheck;
import org.aerogear.mobile.security.checks.DebuggerCheck;
import org.aerogear.mobile.security.checks.EmulatorCheck;
import org.aerogear.mobile.security.checks.EncryptionCheck;
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
     *  Detect if a device is in debug mode
     *  See {@link DebuggerCheck}
     */
    IS_DEBUGGER(new DebuggerCheck()),

    /**
     *  Detect whether the device is emulated.
     *  See {@link EmulatorCheck}
     */
    IS_EMULATOR(new EmulatorCheck()),

    /**
     *  Detect whether a screen lock is enabled (PIN, Password etc).
     *  See {@link ScreenLockCheck}
     */
    SCREEN_LOCK_ENABLED(new ScreenLockCheck()),
    /**
     * Detect whether the allowBackup flag is enabled for the application.
     * See {@link AllowBackupFlagCheck}
     */
    ALLOW_BACKUP_ENABLED(new AllowBackupFlagCheck()),
    /**
     * Detect whether a devices filesystem is encrypted.
     */
    HAS_ENCRYPTION_ENABLED(new EncryptionCheck());


    private SecurityCheck check;

    /**
     * Creates SecurityCheckType object.
     *
     * @param check {@link SecurityCheck}
     */
    SecurityCheckType(SecurityCheck check) {
        this.check = check;
    }

    /**
     * Gets the security check.
     *
     * @return {@link SecurityCheck} instance
     */
    public SecurityCheck getSecurityCheck() {
        return check;
    }

    /**
     * Gets the name of the security check.
     *
     * @return {@link String} {@link SecurityCheck#getName()}
     */
    public String getName() {
        return getSecurityCheck().getName();
    }
}
