package org.aerogear.mobile.security.checks;


import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Detects whether a devices filesystem is encrypted
 */
public class EncryptionCheck extends AbstractSecurityCheck{
    /**
     * Check if a devices filesystem is encrypted
     *
     * @param context Context to be used by the check.
     * @return <code>true</code> if the encryption have been enabled on the device.
     */
    @Override
    protected boolean execute(@NonNull Context context) {
        final DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return policyManager != null && policyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE;
    }
}
