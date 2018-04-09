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
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.logging.Logger;

public class UserIdentityParser {

    private static final Logger LOG = MobileCore.getLogger();
    private static final String TAG = "UserIdentityParser";

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
        this.credential = nonNull(credential, "credential");
        this.keycloakConfiguration = nonNull(keycloakConfiguration, "keycloakConfiguration");
        decodeUserIdentity();
    }

    /**
     * Parse the users first name from the {@link #userIdentity users identity}
     *
     * @return user's first name
     */
    private String parseFirstName() {
        return userIdentity == null ? "" : userIdentity.optString(FIRST_NAME, "");
    }

    /**
     * Parse the users last name from the {@link #userIdentity users identity}
     *
     * @return user's last name
     */
    private String parseLastName() {
        return userIdentity == null ? "" : userIdentity.optString(LAST_NAME, "");
    }

    /**
     * Parses the user's username from the user identity {@link #userIdentity}
     *
     * @return user's username
     */
    private String parseUsername() {
        return userIdentity == null ? "unknown_username"
                        : userIdentity.optString(USERNAME, "unknown_username").trim();
    }

    /**
     * Parses the user's email address from the user identity {@link #userIdentity}
     *
     * @return user's email address
     */
    private String parseEmail() {
        return userIdentity == null ? "" : userIdentity.optString(EMAIL, "").trim();
    }

    /**
     * Parses the user's roles from the user identity {@link #userIdentity}
     *
     * @return user's roles
     */
    private Set<UserRole> parseRoles() {
        Set<UserRole> roles = new HashSet<>();

        Set<UserRole> realmRoles = userIdentity == null ? null : parseRealmRoles();
        roles.addAll(realmRoles);

        Set<UserRole> resourceRoles = userIdentity == null ? null : parseResourceRoles();
        roles.addAll(resourceRoles);

        return roles;
    }

    public UserPrincipalImpl parseUser() {
        return UserPrincipalImpl.newUser().withEmail(parseEmail()).withFirstName(parseFirstName())
                        .withLastName(parseLastName()).withUsername(parseUsername())
                        .withRoles(parseRoles()).withIdentityToken(credential.getIdentityToken())
                        .withAccessToken(credential.getAccessToken())
                        .withRefreshToken(credential.getRefreshToken()).build();
    }

    /**
     * Parses the user's realm roles from the user identity {@link #userIdentity}
     *
     * @return user's realm roles
     */
    private Set<UserRole> parseRealmRoles() {
        Set<UserRole> realmRoles = new HashSet<>();
        try {
            if (userIdentity.has(REALM) && userIdentity.getJSONObject(REALM).has(ROLES)) {
                String tokenRealmRolesJSON = userIdentity.getJSONObject(REALM).getString(ROLES);

                String realmRolesString = tokenRealmRolesJSON
                                .substring(1, tokenRealmRolesJSON.length() - 1).replace("\"", "");
                String roles[] = realmRolesString.split(COMMA);

                for (String roleName : roles) {
                    realmRoles.add(new UserRole(roleName, RoleType.REALM, null));
                }
            }
        } catch (JSONException e) {
            LOG.debug(TAG, "Failed to get user realm roles from user identity", e);
        }
        return realmRoles;
    }

    /**
     * Parses the user's initial resource roles from the user identity {@link #userIdentity}
     *
     * @return user's resource roles
     */
    private Set<UserRole> parseResourceRoles() {
        Set<UserRole> resourceRoles = new HashSet<>();

        if (keycloakConfiguration.getResourceId() != null) {
            String initialResourceID = keycloakConfiguration.getResourceId();

            try {
                if (userIdentity.has(RESOURCE)
                                && userIdentity.getJSONObject(RESOURCE).has(initialResourceID)
                                && userIdentity.getJSONObject(RESOURCE)
                                                .getJSONObject(initialResourceID).has(ROLES)) {
                    String tokenResourceRolesJSON = userIdentity.getJSONObject(RESOURCE)
                                    .getJSONObject(initialResourceID).getString(ROLES);

                    String resourceRolesString = tokenResourceRolesJSON
                                    .substring(1, tokenResourceRolesJSON.length() - 1)
                                    .replace("\"", "");
                    String roles[] = resourceRolesString.split(COMMA);

                    for (String roleName : roles) {
                        resourceRoles.add(new UserRole(roleName, RoleType.RESOURCE,
                                        initialResourceID));
                    }
                }
            } catch (JSONException e) {
                LOG.debug(TAG, "Failed to get users resource roles from user identity", e);
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
