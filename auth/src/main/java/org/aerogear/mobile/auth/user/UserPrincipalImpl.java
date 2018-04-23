package org.aerogear.mobile.auth.user;

import static org.aerogear.mobile.core.utils.SanityCheck.nonEmpty;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class represent an authenticated user
 */
public class UserPrincipalImpl implements UserPrincipal {

    /**
     * The username of the principal.
     */
    private final String username;

    /**
     * The first name of the principal.
     */
    private final String firstName;

    /**
     * The last name of the principal.
     */
    private final String lastName;

    /**
     * The email associated with this user
     */
    private final String email;

    /**
     * Roles associated with this principal.
     */
    private final Set<UserRole> roles;

    /**
     * Identity token. It is used for logout
     */
    private final String identityToken;

    /**
     * Access token for http request authorisation
     */
    private final String accessToken;

    /**
     * The refresh token.
     */
    private final String refreshToken;

    /**
     * Builds a new UserPrincipalImpl object
     *
     * @param username the username of the authenticated user
     * @param firstName the first name of the authenticated user
     * @param lastName the last name of the authenticated user
     * @param email the email of the authenticated user
     * @param roles roles assigned to the user
     * @param identityToken the identity token
     * @param accessToken the access token
     * @param refreshToken the refresh token
     *
     */
    protected UserPrincipalImpl(final String username, final String firstName,
                    final String lastName, final String email, final Set<UserRole> roles,
                    final String identityToken, final String accessToken,
                    final String refreshToken) {
        this.username = nonEmpty(username, "username");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = new HashSet(roles);
        this.identityToken = identityToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Builds and return a UserPrincipalImpl object
     */
    public static class Builder {
        protected String username;
        protected String email;
        protected Set<UserRole> roles = new HashSet();
        protected String idToken;
        protected String accessToken;
        protected String refreshToken;
        protected String firstName;
        protected String lastName;

        protected Builder() {}

        public Builder withFirstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withUsername(final String username) {
            this.username = nonEmpty(username, "username");
            return this;
        }


        public Builder withEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder withRoles(final Set<UserRole> roles) {
            if (roles != null) {
                this.roles.addAll(roles);
            }
            return this;
        }

        public Builder withIdentityToken(final String idToken) {
            this.idToken = idToken;
            return this;
        }

        public Builder withAccessToken(final String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withRefreshToken(final String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public UserPrincipalImpl build() {
            return new UserPrincipalImpl(this.username, this.firstName, this.lastName, this.email,
                            this.roles, this.idToken, this.accessToken, this.refreshToken);
        }
    }

    /**
     * Checks if the user has the specified Resource role.
     *
     * @param role role to be checked
     * @param resourceId resourceId related to role
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean hasResourceRole(final String role, final String resourceId) {
        nonEmpty(role, "role");

        return roles.contains(new UserRole(role, RoleType.RESOURCE, resourceId));
    }

    /**
     * Checks if the user has the specified Realm role.
     *
     * @param role role to be checked
     * @return <code>true</code> or <code>false</code>
     */
    @Override
    public boolean hasRealmRole(final String role) {
        nonEmpty(role, "role");

        return roles.contains(new UserRole(role, RoleType.REALM, null));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /**
     * Get's user roles
     *
     * @return user's roles
     */
    @Override
    public Set<UserRole> getRoles() {
        return roles;
    }

    /**
     * Get's the user realm roles
     *
     * @return user's realm roles
     */
    @Override
    public Set<UserRole> getRealmRoles() {
        return getSpecificRoles(RoleType.REALM);
    }

    /**
     * Get's the user resource roles
     *
     * @return user's resource roles
     */
    @Override
    public Set<UserRole> getResourceRoles() {
        return getSpecificRoles(RoleType.RESOURCE);
    }

    /**
     * Get' specific user roles
     *
     * @param roleType the type of roles to get. Can be either realm or resource
     * @return the requested user roles
     */
    private Set<UserRole> getSpecificRoles(final RoleType roleType) {
        Set<UserRole> specificRoles = new HashSet<>();
        Iterator<UserRole> itr = roles.iterator();
        while (itr.hasNext()) {
            UserRole role = itr.next();
            if (role.getType().equals(roleType)) {
                specificRoles.add(role);
            }
        }
        return specificRoles;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public static Builder newUser() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");

        // TODO: use .stream().map(name -> name.getname())... when API 24
        final Iterator<UserRole> iterator = roles.iterator();
        if (iterator.hasNext()) {
            sb.append(iterator.next().getName());

            while (iterator.hasNext()) {
                sb.append(", ").append(iterator.next().getName());
            }
        }
        sb.append("]");

        return "UserPrincipalImpl{" + "username='" + username + '\'' + ", firstName=" + firstName
                        + '\'' + ", lastName=" + lastName + '\'' + ", email='" + email + '\''
                        + ", roles=" + sb.toString() + '}';
    }

    /**
     * Returns the identity token. It is used during logout.
     *
     * @return the identity token
     */
    @Override
    public String getIdentityToken() {
        return identityToken;
    }

    /**
     * Returns the access token for the current logged user. This token can be added to HTTP
     * requests as the "Authorization: Bearer" header.
     *
     * @return the access token
     */
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns the refresh token.
     *
     * @return the refresh token
     */
    @Override
    public String getRefreshToken() {
        return refreshToken;
    }


    /**
     * Returns the Identity JWT.
     *
     * @return the identity JWT.
     */
    private JSONObject getRawIdentityToken() {
        JSONObject identityJWT = null;
        try {
            String identityToken = getIdentityToken();
            JsonWebSignature signature = new JsonWebSignature();
            signature.setCompactSerialization(identityToken);
            String decoded = signature.getUnverifiedPayload();
            try {
                identityJWT = new JSONObject(decoded);
            } catch (JSONException e) {
                Log.d("Error Getting Attribute", e.getMessage());
            }
        } catch (JoseException e) {
            Log.d("Error Getting Attribute", e.getMessage());
        }
        return identityJWT;
    }

    private boolean customAttributeExists(String attributeName) {
        JSONObject jwt = getRawIdentityToken();
        return jwt != null && jwt.has(attributeName);
    }

    /**
     * Returns the custom user attribute of type string.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom string attribute
     */
    public String getCustomStringAttribute(String attributeName) {
        String attribute = null;
        JSONObject jwt = getRawIdentityToken();

        if (customAttributeExists(attributeName)) {
            attribute = jwt.optString(attributeName);
        }
        return attribute;
    }

    /**
     * Returns the custom user attribute of type boolean.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom boolean attribute
     */
    public Boolean getCustomBooleanAttribute(String attributeName) {
        boolean attribute = false;
        JSONObject jwt = getRawIdentityToken();

        if (customAttributeExists(attributeName)) {
            attribute = jwt.optBoolean(attributeName);
        }
        return attribute;
    }

    /**
     * Returns the custom user attribute of type long.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom long attribute
     */
    public Long getCustomLongAttribute(String attributeName) {
        long attribute = 0;
        JSONObject jwt = getRawIdentityToken();

        if (customAttributeExists(attributeName)) {
            attribute = jwt.optLong(attributeName);
        }
        return attribute;
    }

    /**
     * Returns the custom user attribute of type int.
     *
     * @param attributeName the user attribute to retrieve
     * @return the custom int attribute
     */
    public Integer getCustomIntegerAttribute(String attributeName) {
        int attribute = 0;
        JSONObject jwt = getRawIdentityToken();

        if (customAttributeExists(attributeName)) {
            attribute = jwt.optInt(attributeName);
        }
        return attribute;
    }
}
