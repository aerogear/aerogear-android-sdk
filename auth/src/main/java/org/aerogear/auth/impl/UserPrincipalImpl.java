package org.aerogear.auth.impl;

import org.aerogear.auth.AbstractAuthenticator;
import org.aerogear.auth.AbstractPrincipal;
import org.aerogear.auth.IRole;
import org.aerogear.auth.credentials.ICredential;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represent an authenticated user
 */
public class UserPrincipalImpl extends AbstractPrincipal {

    /**
     * The username of the principal.
     */
    private final String username;

    /**
     * The email associated with this user
     */
    private final String email;

    /**
     * Roles associated with this principal.
     */
    private final Map<String, IRole> roles;

    /**
     * User credentials. It can be null.
     */
    private final ICredential credentials;

    /**
     * Builds a new UserPrincipalImpl object
     *
     * @param username the username of the authenticated user
     * @param email the email of the authenticated user
     * @param roles roles assigned to the user
     * @param authenticator the authenticator that authenticated this user
     */
    private UserPrincipalImpl(final String username,
                              final ICredential credentials,
                              final String email,
                              final Map<String, IRole> roles,
                              final AbstractAuthenticator authenticator) {
        super(authenticator);
        this.username = username;
        this.email = email;
        this.roles = Collections.unmodifiableMap(new HashMap<String, IRole>(roles));
        this.credentials = credentials;
    }

    /**
     * Builds and return a UserPrincipalImpl object
     */
    static class Builder {
        private String username;
        private String email;
        private Map<String, IRole> roles = new HashMap<>();
        private AbstractAuthenticator authenticator;
        protected ICredential credentials;

        private Builder() {
        }

        Builder withUsername(final String username) {
            this.username = username;
            return this;
        }

        Builder withCredentials(final ICredential credentials) {
            this.credentials = credentials;
            return this;
        }

        Builder withEmail(final String email) {
            this.email = email;
            return this;
        }

        Builder withRole(final IRole role) {
            this.roles.put(role.getRoleName(), role);
            return this;
        }

        Builder withRoles(final IRole[] roles) {
            if (roles != null) {
                return withRoles(Arrays.asList(roles));
            } else {
                return this;
            }
        }

        Builder withRoles(final Collection<IRole> roles) {

            if (roles != null) {
                for (IRole role : roles) {
                    this.withRole(role);
                }
            }

            return this;
        }

        Builder withAuthenticator(AbstractAuthenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        UserPrincipalImpl build() {
            return new UserPrincipalImpl(
                    this.username,
                    this.credentials,
                    this.email,
                    this.roles,
                    this.authenticator);
        }
    }

    /**
     * Returns <code>true</code> if the user has the passed in role.
     * @return true or false
     */
    @Override
    public boolean hasRole(final IRole role) {
        return roles.containsKey(role.getRoleName());
    }

    @Override
    public String getName() {
        return username;
    }

    /**
     * Check whether the user is authorized and not expired.
     * @return <code>true</code> if user is authorized and token is not expired.
     */
    @Override
    public boolean checkValidAuth() {
        return getCredentials().isAuthorized() && !getCredentials().getNeedsTokenRefresh();
    }

    @Override
    public Collection<IRole> getRoles() {
        return roles.values();
    }

    @Override
    public ICredential getCredentials() {
        return credentials;
    }

    public static Builder newUser() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UserPrincipalImpl{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles.values() +
                '}';
    }
}
