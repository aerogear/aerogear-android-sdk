package org.aerogear.mobile.auth;

/**
 * Base class for aerogear principals
 */
public abstract class AbstractPrincipal implements IUserPrincipal {

    /**
     * The authenticator that was used to authenticate this principal.
     */
    private final AbstractAuthenticator authenticator;

    /**
     * Type of credentials used to authenticate this user.
     *
     * @param authenticator authenticator that authenticated this principal
     */
    protected AbstractPrincipal (AbstractAuthenticator authenticator) {
        if (authenticator == null) {
            throw new NullPointerException("Authenticator can't be null");
        }

        this.authenticator = authenticator;
    }

    /**
     * Returns the authenticator used to authenticate the principal.
     *
     * @return the authenticator used to authenticate the principal
     */
    public AbstractAuthenticator getAuthenticator() {
        return authenticator;
    }
}
