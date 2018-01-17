package org.aerogear.auth;

/**
 * Base class for roles pojos.
 */
abstract class AbstractRole implements IRole {

    /**
     * Role friendly name
     */
    private final String roleName;

    /**
     * Builds a role object
     * @param roleName Role friendly name
     */
    AbstractRole(final String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getRoleName() {
        return this.roleName;
    }
}
