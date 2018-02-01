package org.aerogear.android.ags.auth;

/**
 * Creates a new keycloak realm role for a user
 */
public final class RealmRole extends AbstractRole {
    public RealmRole(final String roleName) {
        super(roleName);
    }

    /**
     * Get's the role type
     *
     * @return the role type
     * @see RoleType
     */
    @Override
    public RoleType getRoleType() {
        return RoleType.REALM;
    }
}
