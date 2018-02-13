package org.aerogear.mobile.auth.user;

import org.aerogear.mobile.auth.authenticator.AbstractAuthenticator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * This class represent an authenticated user
 */
public class UserPrincipalImpl implements UserPrincipal {

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
    private final Set<UserRole> roles;

    /**
     * Builds a new UserPrincipalImpl object
     *
     * @param username the username of the authenticated user
     * @param email the email of the authenticated user
     * @param roles roles assigned to the user
     */
    protected UserPrincipalImpl(final String username,
                              final String email,
                              final Set<UserRole> roles) {
        this.username = username;
        this.email = email;
        this.roles = Collections.synchronizedSet(new HashSet<>(roles));
    }

    /**
     * Builds and return a UserPrincipalImpl object
     */
    public static class Builder {
        protected String username;
        protected String email;
        protected HashSet<UserRole> roles = new HashSet<>();

        protected Builder() {
        }

        public Builder withUsername(final String username) {
            this.username = username;
            return this;
        }


        public Builder withEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder withRoles(final Set<UserRole> roles) {
            if (roles != null) {
                this.roles.addAll(roles);
            }
            return this;
        }

        public UserPrincipalImpl build() {
            return new UserPrincipalImpl(
                    this.username,
                    this.email,
                    this.roles);
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
        return roles.contains(new UserRole(role, RoleType.CLIENT, clientId));
    }

    /**
     * Checks if the user has the specified Realm role.
     * @param role role to be checked
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean hasRealmRole(final String role){
        return roles.contains(new UserRole(role, RoleType.REALM, null));
    }

    @Override
    public String getName() {
        return username;
    }

    /**
     * Get's user roles
     *
     * @return user's roles
     */
    @Override
    public Set<UserRole> getRoles() {
       return roles;
    }

    public static Builder newUser() {
        return new Builder();
    }

    @Override
    public String toString() {
        String roleNames = "";
        Iterator<UserRole> i = roles.iterator();
        if (i.hasNext()) {
            //first element
            roleNames.concat("[").concat(i.next().getName());
            while(i.hasNext()) {
                roleNames.concat(", ").concat(i.next().getName());
            }
        }
        roleNames.concat("]");

        return "UserPrincipalImpl{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roleNames +
                '}';
    }
}
