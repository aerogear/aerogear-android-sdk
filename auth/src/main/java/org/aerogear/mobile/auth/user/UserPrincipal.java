package org.aerogear.mobile.auth.user;

import java.io.Serializable;
import java.util.Set;

/**
 * Public interface for user principals.
 * It extends the {@link Serializable} interface to allow instances to be easily pass around between {@link android.app.Activity} and {@link android.app.Fragment} using {@link android.os.Bundle}.
 */
public interface UserPrincipal extends Serializable {

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

    /**
     * Returns the token that will allow access to other services.
     * @return the access token.
     */
    String getAccessToken();
}
