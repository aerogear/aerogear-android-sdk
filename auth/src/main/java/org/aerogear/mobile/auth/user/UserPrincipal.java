package org.aerogear.mobile.auth.user;

import java.io.Serializable;
import java.util.Set;

/**
 * Public interface for user principals. It extends the {@link Serializable} interface to allow
 * instances to be easily pass around between {@link android.app.Activity} and
 * {@link android.app.Fragment} using {@link android.os.Bundle}.
 */
public interface UserPrincipal extends Serializable {

    /**
     * Checks if the user has the specified Client role.
     * 
     * @param role role to be checked
     * @param clientId clientID related to role
     * @return true or false
     */
    boolean hasClientRole(String role, String clientId);

    /**
     * Checks if the user has the specified Realm role.
     * 
     * @param role role to be checked
     * @return true or false
     */
    boolean hasRealmRole(String role);

    /**
     * Returns the username
     * 
     * @return the username
     */
    String getUsername();

    /**
     * Returns the full name of the user. Both first and last name.
     * 
     * @return users full name
     */
    String getName();

    /**
     * Returns the first name of the user.
     * 
     * @return users first name.
     */
    String getFirstName();

    /**
     * Returns the last name of the user.
     * 
     * @return users last name.
     */
    String getLastName();

    /**
     * Returns the email
     * 
     * @return the email
     */
    String getEmail();

    /**
     * Returns the roles associated with this principal
     * 
     * @return the roles associated with this principal
     */
    Set<UserRole> getRoles();

    /**
     * Returns the token that will allow access to other services.
     * 
     * @return the access token.
     */
    String getAccessToken();

    /**
     * Returns the users identity token.
     * 
     * @return the identity token.
     */
    String getIdentityToken();

    /**
     * Returns the users refresh token.
     * 
     * @return the refresh token.
     */
    String getRefreshToken();
}
