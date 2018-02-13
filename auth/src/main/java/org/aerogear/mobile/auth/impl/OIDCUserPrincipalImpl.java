package org.aerogear.mobile.auth.impl;

import org.aerogear.mobile.auth.AbstractAuthenticator;
import org.aerogear.mobile.auth.UserRole;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;

import java.util.Set;

public class OIDCUserPrincipalImpl extends UserPrincipalImpl {

    /**
     * Builds a new UserPrincipalImpl object
     *
     * @param username the username of the authenticated user
     * @param credentials the OIDC credentials of the user
     * @param email the email of the authenticated user
     * @param roles roles assigned to the user
     * @param authenticator the authenticator that authenticated this user
     */
    protected OIDCUserPrincipalImpl(final String username,
                             final OIDCCredentials credentials,
                             final String email,
                             final Set<UserRole> roles,
                             final AbstractAuthenticator authenticator) {
        super(username, credentials, email, roles, authenticator);
    }

    static class Builder extends UserPrincipalImpl.Builder {
        private Builder() {
            super();
        }

        @Override
        OIDCUserPrincipalImpl build() {
            return new OIDCUserPrincipalImpl(
                    this.username,
                    (OIDCCredentials) this.credentials,
                    this.email,
                    this.roles,
                    this.authenticator);
        }
    }

    public static Builder newUser() { return new Builder(); }
}
