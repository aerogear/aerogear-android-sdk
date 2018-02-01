package org.aerogear.auth;
import java.util.Objects;

/**
 * Stores a user's keycloak roles information
 */

public class RoleKey {

    private final String roleName;
    private final RoleType roleType;
    private final String clientID;

    /**
     * Creates a new RoleKey object
     *
     * @param role
     * @param clientID
     */
    public RoleKey(final IRole role, final String clientID) {
        roleName = role.getRoleName();
        roleType = role.getRoleType();
        this.clientID = clientID;
    }

    /**
     * Creates a new RoleKey object
     *
     * @param roleName
     * @param roleType
     * @param clientId
     */
    public RoleKey(final String roleName, final String clientId, final RoleType roleType){
        this.roleName = roleName;
        this.roleType = roleType;
        this.clientID = clientId;
    }

    /**
     * Compares RoleKey objects for equality
     *
     * @param roleKey
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean equals(final Object roleKey) {
        if (this == roleKey) return true;
        if (roleKey == null || roleKey.getClass() != getClass()) return false;
        if (((RoleKey) roleKey).roleType == RoleType.CLIENT) { //do a check on clientID
            return ((RoleKey) roleKey).roleName.equals(roleName) && ((RoleKey) roleKey).roleType == roleType &&
                ((RoleKey) roleKey).clientID == clientID;
        } else {
            return ((RoleKey) roleKey).roleName.equals(roleName) && ((RoleKey) roleKey).roleType == roleType;
        }
    }

    /**
     * Generates hashcode value from the RoleKey rolename {@link #roleName} and roleType {@link #roleType}
     *
     * @return hashcode value
     */
    @Override
    public int hashCode(){
        return Objects.hash(roleName, roleType);
    }
}
