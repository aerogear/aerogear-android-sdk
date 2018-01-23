package org.aerogear.android.ags.auth;

/**
 * Creates a new keycloak client role for a user
 */
public final class ClientRole extends AbstractRole {

    private final String clientID;

    /**
     * Creates a ClientRole object
     *
     * @param roleName
     * @param clientID
     */
    public ClientRole(final String roleName, String clientID) {
        super(roleName);
        this.clientID = clientID;
    }

    /**
     * Get's the role type
     *
     * @return the role type
     * @see RoleType
     */
    @Override
    public RoleType getRoleType() {
        return RoleType.CLIENT;
    }

    /**
     * Get's the client id for the client role
     *
     * @return the client id
     */
    @Override
    public String getClientID() {
        return clientID;
    }
}
