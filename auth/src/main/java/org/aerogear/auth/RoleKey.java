package org.aerogear.auth;
import java.util.Objects;
import org.aerogear.auth.impl.UserPrincipalImpl;

/**
 * Stores a user's keycloak roles information
 */

public class RoleKey {

    private String roleName;
    private RoleType roleType;
    private String clientID;

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

    public RoleKey(final String roleName, final String clientId, final RoleType roleType){
        this.roleName = roleName;
        this.clientID = clientId;
        this.roleType = roleType;
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

    @Override
    public int hashCode(){
        return Objects.hash(roleName, roleType);
    }
}
