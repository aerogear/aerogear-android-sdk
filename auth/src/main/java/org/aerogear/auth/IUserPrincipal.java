package org.aerogear.auth;

import org.aerogear.auth.credentials.ICredential;

import java.security.Principal;
import java.util.Collection;

/**
 * Public interface for user principals.
 */
public interface IUserPrincipal extends Principal {

    /**
     * Checks if the user has the specified role.
     * @param role role to be checked
     * @return true or false
     */
    boolean hasRole(IRole role);

    /**
     * Returns the username
     * @return the username
     */
    String getName();

    /**
     * Returns the roles associated with this principal
     * @return the roles associated with this principal
     */
    Collection<IRole> getRoles();

    /**
     * Returns the credentials that authenticate this users.
     * It can be null, or obfuscated bytes.
     * @return the credentials that authenticate this users
     */
    ICredential getCredentials();
}
