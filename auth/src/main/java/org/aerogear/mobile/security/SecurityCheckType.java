package org.aerogear.mobile.security;


import org.aerogear.mobile.security.checks.BackupDisallowedCheck;
import org.aerogear.mobile.security.checks.DeveloperModeDisabledCheck;
import org.aerogear.mobile.security.checks.EncryptionCheck;
import org.aerogear.mobile.security.checks.NoDebuggerCheck;
import org.aerogear.mobile.security.checks.NonRootedCheck;
import org.aerogear.mobile.security.checks.NotInEmulatorCheck;
import org.aerogear.mobile.security.checks.ScreenLockCheck;

/**
 * Checks that can be performed.
 */
public enum SecurityCheckType {

    /**
     * Detect whether the device is rooted. See {@link NonRootedCheck}
     */
    NOT_ROOTED(new NonRootedCheck()),

    /**
     * Detect if developer mode is enabled in the device. See {@link DeveloperModeDisabledCheck}
     */
    NO_DEVELOPER_MODE(new DeveloperModeDisabledCheck()),

    /**
     * Detect if a device is in debug mode See {@link NoDebuggerCheck}
     */
    NO_DEBUGGER(new NoDebuggerCheck()),

    /**
     * Detect whether the device is emulated. See {@link NotInEmulatorCheck}
     */
    NOT_IN_EMULATOR(new NotInEmulatorCheck()),

    /**
     * Detect whether a screen lock is enabled (PIN, Password etc). See {@link ScreenLockCheck}
     */
    SCREEN_LOCK_ENABLED(new ScreenLockCheck()),
    /**
     * Detect whether the allowBackup flag is enabled for the application. See
     * {@link BackupDisallowedCheck}
     */
    ALLOW_BACKUP_DISABLED(new BackupDisallowedCheck()),
    /**
     * Detect whether a devices filesystem is encrypted. See {@link EncryptionCheck}
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
     * Gets the type of the security check.
     *
     * @return {@link String} {@link SecurityCheck#getId()}
     */
    public String getType() {
        return getSecurityCheck().getId();
    }
}
