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
     * Checks if the user has the specified Resource role.
     *
     * @param role role to be checked
     * @param resourceId resourceId related to role
     * @return true or false
     */
    boolean hasResourceRole(String role, String resourceId);

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
     * Returns the realm roles associated with this principal
     *
     * @return the realm roles associated with this principal
     */
    Set<UserRole> getRealmRoles();

    /**
     * Returns the resource roles associated with this principal
     *
     * @return the resource roles associated with this principal
     */
    Set<UserRole> getResourceRoles();

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

    /**
     * Returns the custom user attribute of type string.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom string attribute
     */
    String getCustomStringAttribute(String attributeName);

    /**
     * Returns the custom user attribute of type boolean.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom boolean attribute
     */
    Boolean getCustomBooleanAttribute(String attributeName);

    /**
     * Returns the custom user attribute of type long.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom long attribute
     */
    Long getCustomLongAttribute(String attributeName);

    /**
     * Returns the custom user attribute of type int.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom int attribute
     */
    Integer getCustomIntegerAttribute(String attributeName);
}
