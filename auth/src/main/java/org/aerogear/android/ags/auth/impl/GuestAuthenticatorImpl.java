package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.IRole;
import org.aerogear.android.ags.auth.credentials.ICredential;

import java.security.Principal;
import java.util.Arrays;

/**
 * Simple authenticator to return 'guest' users.
 * No authentication is performed at all.
 */
public class GuestAuthenticatorImpl extends AbstractAuthenticator {

    /**
     * The 'guest' username to return.
     */
    private final String guestUser;

    /**
     * Roles to be assigned to the guest user
     */
    private final IRole[] roles;

    /**
     * Builds a new guest authenticator object
     * @param guestUser the user to be returned after the 'authentication'
     * @param roles the roles to be assigned to the user
     */
    public GuestAuthenticatorImpl(final String guestUser, final IRole[] roles) {
        super(null);
        this.guestUser = guestUser;
        if (roles == null) {
            this.roles = new IRole[0];
        } else {
            this.roles = Arrays.copyOf(roles, roles.length);
        }
    }

    /**
     * Simply returns a user with username {@link #guestUser}
     * @param credential user credential
     * @return a user with username {@link #guestUser}
     * @throws AuthenticationException
     */
    public Principal authenticate(final ICredential credential) {
        return UserPrincipalImpl
                .newUser()
                .withAuthenticator(this)
                .withUsername(guestUser)
                .withRoles(roles)
                .build();
    }

    @Override
    public void logout(Principal principal) {
        return;
    }
}
