package org.aerogear.mobile.security.checks;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;

/**
 * Check to determine whether the allowBackup flag is enabled for the application. The allowBackup
 * flag determines whether to allow the application to participate in the backup and restore
 * infrastructure.
 */
public class AllowBackupFlagCheck extends AbstractSecurityCheck {
    /**
     * Check whether the allowBackup flag is enabled.
     *
     * @param context Context to be used by the check.
     * @return true if allowBackup is enabled.
     * @throws IllegalStateException Will be thrown if package information can not be found.
     */
    @Override
    public boolean execute(@NonNull final Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            throw new IllegalStateException(
                            "Could not retrieve package information from provided context", e);
        }
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0;
    }

    @Override
    public String getName() {
        return "Allow Backup Flag Check";
    }
}
