package org.aerogear.mobile.auth.user;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user's keycloak roles information.
 */
public class UserRole implements Serializable {

    /**
     * Role name. Can't be null.
     */
    private final String name;

    /**
     * Role type. Can't be null.
     */
    private final RoleType type;

    /**
     * Role name space/client ID.  Can be null.
     */
    private final String namespace;

    /**
     * Creates a new UserRole object.
     *
     * @param name role name.
     * @param type role type.
     * @param namespace role name space/client ID.
     */
    public UserRole(final String name, final RoleType type, final String namespace) {
        if (name == null || type == null) {
            throw new IllegalArgumentException("Role name and type cannot be null");
        } else {
            this.name = name;
            this.type = type;
        }
        this.namespace = namespace;
    }

    /**
     * Get's the name of the role.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get's the type of the role.
     *
     * @return type.
     */
    public RoleType getType() {
        return type;
    }

    /**
     * Get's the namespace/client ID of the role.
     *
     * @return namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Compares UserRole objects for equality.
     *
     * @param role a UserRole object.
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean equals(final Object role) {
        if (this == role) return true;
        if (role == null || role.getClass() != getClass()) return false;
        UserRole userRole = (UserRole) role;
        if (userRole.type == RoleType.CLIENT) { //do a check on clientID
            return userRole.name.equals(name) && userRole.type == type &&
                userRole.namespace == namespace;
        } else {
            return userRole.name.equals(name) && userRole.type == type;
        }
    }

    /**
     *  Generates hashcode value from the UserRole name {@link #name} and type {@link #type}
     *
     * @return hashcode value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        if (this.namespace != null) {
            return this.namespace + ":" + this.name;
        } else {
            return this.name;
        }
    }
}
