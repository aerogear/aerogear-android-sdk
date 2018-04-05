package org.aerogear.mobile.auth.utils;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.HashSet;
import java.util.Set;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;

import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.RoleType;
import org.aerogear.mobile.auth.user.UserPrincipalImpl;
import org.aerogear.mobile.auth.user.UserRole;

public class UserIdentityParser {

    private static final String USERNAME = "preferred_username";
    private static final String EMAIL = "email";
    private static final String REALM = "realm_access";
    private static final String RESOURCE = "resource_access";
    private static final String ROLES = "roles";
    private static final String FIRST_NAME = "given_name";
    private static final String LAST_NAME = "family_name";
    private static final String COMMA = ",";

    /**
     * The parsed keycloak singleThreadService configuration {@link KeycloakConfiguration}. Should
     * be initialised before using this parser.
     */
    private final KeycloakConfiguration keycloakConfiguration;

    /**
     * The user's identity decoded from their credentials. Can be null.
     */
    private JSONObject userIdentity = new JSONObject();

    // TODO: use JwtClaims instead of using the raw credential instance
    private OIDCCredentials credential;

    public UserIdentityParser(final OIDCCredentials credential,
                    final KeycloakConfiguration keycloakConfiguration)
                    throws AuthenticationException {
        this.credential = credential;
        if (credential != null) {
            decodeUserIdentity();
        }
        this.keycloakConfiguration = nonNull(keycloakConfiguration, "keycloakConfiguration");
    }

    /**
     * Parse the users first name from the {@link #userIdentity users identity}
     *
     * @return user's first name
     * @throws JSONException if the {@link #FIRST_NAME} property cannot be retrieved from the user
     *         identity.
     */
    public String parseFirstName() throws JSONException {
        String firstName = "";
        if (userIdentity != null && userIdentity.has(FIRST_NAME)) {
            firstName = userIdentity.getString(FIRST_NAME);
        }
        return firstName;
    }

    /**
     * Parse the users last name from the {@link #userIdentity users identity}
     *
     * @return user's last name
     * @throws JSONException if the {@link #LAST_NAME} property cannot be retrieved from the user
     *         identity.
     */
    public String parseLastName() throws JSONException {
        String lastName = "";
        if (userIdentity != null && userIdentity.has(LAST_NAME)) {
            lastName = userIdentity.getString(LAST_NAME);
        }
        return lastName;
    }

    /**
     * Parses the user's username from the user identity {@link #userIdentity}
     *
     * @return user's username
     * @throws JSONException if the USERNAME property is not in the userIdentity object
     */
    public String parseUsername() throws JSONException {
        String username = "Unknown Username";
        if (userIdentity != null) {
            // get the users username
            if (userIdentity.has(USERNAME) && userIdentity.getString(USERNAME).length() > 0) {
                username = userIdentity.getString(USERNAME);
            }
        }
        return username;
    }

    /**
     * Parses the user's email address from the user identity {@link #userIdentity}
     *
     * @return user's email address
     * @throws JSONException if the EMAIL property is not in the userIdentity object
     */
    public String parseEmail() throws JSONException {
        String emailAddress = "Unknown Email";
        if (userIdentity != null) {
            // get the users email
            if (userIdentity.has(EMAIL) && userIdentity.getString(EMAIL).length() > 0) {
                emailAddress = userIdentity.getString(EMAIL);
            }
        }
        return emailAddress;
    }

    /**
     * Parses the user's roles from the user identity {@link #userIdentity}
     *
     * @return user's roles
     * @throws JSONException if the REALM property is not in the userIdentity object
     */
    public Set<UserRole> parseRoles() throws JSONException {
        Set<UserRole> roles = new HashSet<>();
        if (userIdentity != null) {
            Set<UserRole> realmRoles = parseRealmRoles();
            if (realmRoles != null) {
                roles.addAll(realmRoles);
            }
            Set<UserRole> resourceRoles = parseResourceRoles();
            if (resourceRoles != null) {
                roles.addAll(resourceRoles);
            }
        }
        return roles;
    }

    public UserPrincipalImpl parseUser() throws AuthenticationException {
        try {
            return UserPrincipalImpl.newUser().withEmail(parseEmail())
                            .withFirstName(parseFirstName()).withLastName(parseLastName())
                            .withUsername(parseUsername()).withRoles(parseRoles())
                            .withIdentityToken(credential.getIdentityToken())
                            .withAccessToken(credential.getAccessToken())
                            .withRefreshToken(credential.getRefreshToken()).build();
        } catch (JSONException jsonEx) {
            throw new AuthenticationException(jsonEx);
        }
    }

    /**
     * Parses the user's realm roles from the user identity {@link #userIdentity}
     *
     * @return user's realm roles
     * @throws JSONException if the REALM property is not in the userIdentity object
     */
    private Set<UserRole> parseRealmRoles() throws JSONException {
        Set<UserRole> realmRoles = new HashSet<>();
        if (userIdentity.has(REALM) && userIdentity.getJSONObject(REALM).has(ROLES)) {
            String tokenRealmRolesJSON = userIdentity.getJSONObject(REALM).getString(ROLES);

            String realmRolesString = tokenRealmRolesJSON
                            .substring(1, tokenRealmRolesJSON.length() - 1).replace("\"", "");
            String roles[] = realmRolesString.split(COMMA);

            for (String roleName : roles) {
                UserRole realmRole = new UserRole(roleName, RoleType.REALM, null);
                realmRoles.add(realmRole);
            }
        }
        return realmRoles;
    }

    /**
     * Parses the user's initial resource roles from the user identity {@link #userIdentity}
     *
     * @return user's resource roles
     * @throws JSONException if the RESOURCE property is not in the userIdentity object or RESOURCE
     *         does not have a ROLES property
     */
    private Set<UserRole> parseResourceRoles() throws JSONException {
        Set<UserRole> resourceRoles = new HashSet<>();

        if (keycloakConfiguration.getResourceId() != null) {
            String initialResourceID = keycloakConfiguration.getResourceId();

            if (userIdentity.has(RESOURCE)
                            && userIdentity.getJSONObject(RESOURCE).has(initialResourceID)
                            && userIdentity.getJSONObject(RESOURCE).getJSONObject(initialResourceID)
                                            .has(ROLES)) {
                String tokenResourceRolesJSON = userIdentity.getJSONObject(RESOURCE)
                                .getJSONObject(initialResourceID).getString(ROLES);

                String resourceRolesString = tokenResourceRolesJSON
                                .substring(1, tokenResourceRolesJSON.length() - 1)
                                .replace("\"", "");
                String roles[] = resourceRolesString.split(COMMA);

                for (String roleName : roles) {
                    UserRole resourceRole =
                                    new UserRole(roleName, RoleType.RESOURCE, initialResourceID);
                    resourceRoles.add(resourceRole);
                }
            }
        }
        return resourceRoles;
    }

    /**
     * Gets the user's identity by decoding the user's access token
     * {@link OIDCCredentials#getAccessToken()}
     *
     * @return user's identity
     * @throws AuthenticationException if the user access token could not be decoded.
     */
    private void decodeUserIdentity() throws AuthenticationException {
        String accessToken = ((OIDCCredentials) credential).getAccessToken();

        try {
            // Decode the Access Token to Extract the Identity Information
            JsonWebSignature signature = new JsonWebSignature();
            signature.setCompactSerialization(accessToken);
            // Note: this does not verify the token
            String decoded = signature.getUnverifiedPayload();
            try {
                userIdentity = new JSONObject(decoded);
            } catch (JSONException e) {
                throw new AuthenticationException(e.getMessage(), e.getCause());
            }

        } catch (JoseException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
    }
}
