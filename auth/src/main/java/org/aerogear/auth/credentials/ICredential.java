package org.aerogear.auth.credentials;

/**
 * Base interface for credential objects.
 */
public interface ICredential {

    boolean getNeedsTokenRefresh();
    void setNeedsTokenRefresh();
    boolean isAuthorized();

}
