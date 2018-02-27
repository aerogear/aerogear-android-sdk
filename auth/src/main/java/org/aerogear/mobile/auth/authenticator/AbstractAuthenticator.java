package org.aerogear.mobile.auth.authenticator;

import android.support.annotation.NonNull;

import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Base class for all authenticators
 */
public abstract class AbstractAuthenticator {

    /**
     * Authentication singleThreadService configuration.
     */
    private final ServiceConfiguration serviceConfig;


    public AbstractAuthenticator(@NonNull final ServiceConfiguration serviceConfig) {

        this.serviceConfig = nonNull(serviceConfig, "serviceConfig");
    }

    /**
     * This method must be overridden with the custom authentication for the given credential.
     *
     * @param authOptions the options for the authenticate action
     * @param callback    the callback function to be invoked
     */
    public abstract void authenticate(final AuthenticateOptions authOptions, final Callback<UserPrincipal> callback);

    /**
     * Logout the given principal
     *
     * @param principal principal to be log out
     */
    public abstract void logout(final UserPrincipal principal);

    /**
     * Returns the authentication singleThreadService configuration
     *
     * @return the authentication singleThreadService configuration
     */
    protected ServiceConfiguration getServiceConfig() {
        return this.serviceConfig;
    }
}
