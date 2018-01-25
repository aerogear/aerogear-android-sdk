package org.aerogear.auth.credentials;

/**
 * Base interface for credential objects.
 */
public interface ICredential {

    /**
     * Check whether credential needs to be renewed
     * @return <code>true</code> if the credential needs to be renewed
     */
    boolean getNeedsRenewal();

    /**
     * Set the credential to be renewed whether it needs to or not.
     * This is a way to force the renewal of the credential.
     */
    void setNeedsRenewal();

    /**
     * Check whether the credential is authenticated/authorized.
     * @return <code>true</code> if the credential is authorized.
     */
    boolean isAuthorized();

    /**
     * Check whether the credential is invalid in any way (expired, invalid).
     * @return <code>true</code> if the credential is valid and usable.
     */
    boolean checkValidAuth();
}
