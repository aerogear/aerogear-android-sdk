package org.aerogear.mobile.core.http.pinning;

/**
 * Listener for certificate pinning checks.
 *
 * A check can be: * Successful - The certificate pinning check completed without any issues *
 * Failure - The certificate pinning check failed, for one or many reasons
 *
 * The reasons for the certificate pinning check may not be related to pinning itself. For example,
 * a failed network request might have caused the failure. However if successful this means that the
 * pinning check definitely succeeded.
 */
public interface CertificatePinningCheckListener {
    /**
     * Invoked when the check has completed without any issues.
     */
    void onSuccess();

    /**
     * Invoked when the check has failed. This could be for various reasons including a pinning
     * failure.
     */
    void onFailure();
}
