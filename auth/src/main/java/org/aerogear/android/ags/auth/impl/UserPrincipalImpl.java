package org.aerogear.android.ags.auth.impl;

import org.aerogear.auth.AbstractAuthenticator;
import org.aerogear.auth.AbstractPrincipal;
import org.aerogear.auth.IRole;
import org.aerogear.auth.RoleKey;
import org.aerogear.auth.RoleType;
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
    private final Map<RoleKey, IRole> roles;

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
    protected UserPrincipalImpl(final String username,
                              final ICredential credentials,
                              final String email,
                              final Map<RoleKey, IRole> roles,
                              final AbstractAuthenticator authenticator) {
        super(authenticator);
        this.username = username;
        this.email = email;
        this.roles = Collections.unmodifiableMap(new HashMap<RoleKey, IRole>(roles));
        this.credentials = credentials;
    }

    /**
     * Builds and return a UserPrincipalImpl object
     */
    static class Builder {
        protected String username;
        protected String email;
        protected Map<RoleKey, IRole> roles = new HashMap<>();
        protected AbstractAuthenticator authenticator;
        protected ICredential credentials;

        protected Builder() {
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
            RoleKey roleKey = role.getRoleType().equals(RoleType.CLIENT) ? new RoleKey(role, role.getClientID()) : new RoleKey(role, null);
            this.roles.put(roleKey, role);
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
     * Checks if the user has the specified Client role.
     * @param role role to be checked
     * @param clientId clientID related to role
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean hasClientRole(final String role, final String clientId) {
        return roles.containsKey(new RoleKey(role, clientId, RoleType.CLIENT));
    }

    /**
     * Checks if the user has the specified Realm role.
     * @param role role to be checked
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean hasRealmRole(final String role){
        return roles.containsKey(new RoleKey(role, null, RoleType.REALM));
    }

    @Override
    public String getName() {
        return username;
    }

    /**
     * Gets user roles
     *
     * @return roles
     */
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
