package org.aerogear.mobile.auth.authenticator;

import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.security.Principal;

/**
 * Base class for all authenticators
 */
public class AbstractAuthenticator {

    /**
     * Authentication service configuration.
     */
    private final ServiceConfiguration serviceConfig;


    public AbstractAuthenticator(final ServiceConfiguration serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    /**
     * This method must be overridden with the custom authentication for the given credential.
     *
     * @param authOptions the options for the authenticate action
     * @param callback the callback function to be invoked
     */
    public void authenticate(final AuthenticateOptions authOptions, final Callback<UserPrincipal> callback) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Logout the given principal
     *
     * @param principal principal to be log out
     */
    public void logout(final UserPrincipal principal) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Returns the authentication service configuration
     *
     * @return the authentication service configuration
     */
    public ServiceConfiguration getServiceConfig() {
        return this.serviceConfig;
    }
}
