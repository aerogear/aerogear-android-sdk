package org.aerogear.mobile.auth.user;

import java.io.Serializable;

/**
 * Keycloak role types
 */
public enum RoleType implements Serializable {

    /**
     * Represents a Keycloak realm.
     */
    REALM,

    /**
     * Represents a Keycloak resource.
     */
    RESOURCE
}
