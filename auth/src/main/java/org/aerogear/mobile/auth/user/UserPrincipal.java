package org.aerogear.mobile.auth.user;

import java.security.Principal;
import java.util.Set;

/**
 * Public interface for user principals.
 */
public interface UserPrincipal extends Principal {

    /**
     * Checks if the user has the specified Client role.
     * @param role role to be checked
     * @param clientId clientID related to role
     * @return true or false
     */
    boolean hasClientRole(String role, String clientId);

    /**
     * Checks if the user has the specified Realm role.
     * @param role role to be checked
     * @return true or false
     */
    boolean hasRealmRole(String role);

    /**
     * Returns the username
     * @return the username
     */
    String getName();

    /**
     * Returns the email
     * @return the email
     */
    String getEmail();
    /**
     * Returns the roles associated with this principal
     * @return the roles associated with this principal
     */
    Set<UserRole> getRoles();
}
