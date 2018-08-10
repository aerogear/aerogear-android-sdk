package org.aerogear.mobile.security;


import org.aerogear.mobile.security.checks.BackupEnabledCheck;
import org.aerogear.mobile.security.checks.DebuggerEnabledCheck;
import org.aerogear.mobile.security.checks.DeveloperModeEnabledCheck;
import org.aerogear.mobile.security.checks.EncryptionEnabledCheck;
import org.aerogear.mobile.security.checks.IsEmulatorCheck;
import org.aerogear.mobile.security.checks.RootEnabledCheck;
import org.aerogear.mobile.security.checks.ScreenLockEnabledCheck;

/**
 * Checks that can be performed.
 */
public enum DeviceCheckType {

    /**
     * Detect whether the device is rooted. See {@link RootEnabledCheck}
     */
    ROOT_ENABLED(new RootEnabledCheck()),

    /**
     * Detect if developer mode is enabled in the device. See {@link DeveloperModeEnabledCheck}
     */
    DEVELOPER_MODE_ENABLED(new DeveloperModeEnabledCheck()),

    /**
     * Detect if a device is in debug mode See {@link DebuggerEnabledCheck}
     */
    DEBUGGER_ENABLED(new DebuggerEnabledCheck()),

    /**
     * Detect whether the device is emulated. See {@link IsEmulatorCheck}
     */
    IS_EMULATOR(new IsEmulatorCheck()),

    /**
     * Detect whether a screen lock is enabled (PIN, Password etc). See
     * {@link ScreenLockEnabledCheck}
     */
    SCREEN_LOCK_ENABLED(new ScreenLockEnabledCheck()),
    /**
     * Detect whether the allowBackup flag is enabled for the application. See
     * {@link BackupEnabledCheck}
     */
    BACKUP_ENABLED(new BackupEnabledCheck()),
    /**
     * Detect whether a devices filesystem is encrypted. See {@link EncryptionEnabledCheck}
     */
    ENCRYPTION_ENABLED(new EncryptionEnabledCheck());


    private DeviceCheck check;

    /**
     * Creates DeviceCheckType object.
     *
     * @param check {@link DeviceCheck}
     */
    DeviceCheckType(DeviceCheck check) {
        this.check = check;
    }

    /**
     * Gets the device check.
     *
     * @return {@link DeviceCheck} instance
     */
    public DeviceCheck getDeviceCheck() {
        return check;
    }

    /**
     * Gets the type of the device check.
     *
     * @return {@link String} {@link DeviceCheck#getId()}
     */
    public String getType() {
        return getDeviceCheck().getId();
    }
}
