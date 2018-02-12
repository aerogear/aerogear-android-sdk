package org.aerogear.android.ags.auth.utils;

import android.util.Base64;

import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.RoleType;
import org.aerogear.android.ags.auth.UserRole;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class UserIdentityParser {

    private static final String USERNAME = "preferred_username";
    private static final String EMAIL = "email";
    private static final String REALM = "realm_access";
    private static final String CLIENT = "resource_access";
    private static final String ROLES = "roles";
    private static final String RESOURCE = "resource";
    private static final String COMMA = ",";

    /**
     * User's credentials. Can be null.
     */
    private final ICredential credential;

    /**
     * The parsed keycloak service configuration {@link ServiceConfiguration}.
     * Should be initialised before using this parser.
     */
    private final ServiceConfiguration serviceConfiguration;

    /**
     * The user's identity decoded from their credentials. Can be null.
     */
    private JSONObject userIdentity = new JSONObject();

    public UserIdentityParser(final ICredential credential, final ServiceConfiguration serviceConfiguration) throws JSONException, AuthenticationException {
        this.credential = credential;
        if (credential != null) {
            decodeUserIdentity();
        }
        if (serviceConfiguration == null) {
            throw new IllegalArgumentException("The Keycloak service configuration has not yet been initialised");
        } else {
            this.serviceConfiguration = serviceConfiguration;
        }
    }

    /**
     * Parses the user's username from the user identity {@link #userIdentity}
     *
     * @return user's username
     * @throws JSONException
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
     * @throws JSONException
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
     * @throws JSONException
     */
    public Set<UserRole> parseRoles() throws JSONException {
        Set<UserRole> roles = new HashSet<>();
        if (userIdentity != null) {
            Set<UserRole> realmRoles = parseRealmRoles();
            if (realmRoles != null) {
                roles.addAll(realmRoles);
            }
            Set<UserRole> clientRoles = parseClientRoles();
            if (clientRoles != null) {
                roles.addAll(clientRoles);
            }
        }
        return roles;
    }

    /**
     * Parses the user's realm roles from the user identity {@link #userIdentity}
     *
     * @return user's realm roles
     * @throws JSONException
     */
    private Set<UserRole> parseRealmRoles() throws JSONException {
        Set<UserRole> realmRoles = new HashSet<>();
        if (userIdentity.has(REALM) && userIdentity.getJSONObject(REALM).has(ROLES)) {
            String tokenRealmRolesJSON = userIdentity.getJSONObject(REALM).getString(ROLES);

            String realmRolesString = tokenRealmRolesJSON.substring(1, tokenRealmRolesJSON.length() - 1).replace("\"", "");
            String roles[] = realmRolesString.split(COMMA);

            for (String roleName : roles) {
                UserRole realmRole = new UserRole(roleName, RoleType.REALM, null);
                realmRoles.add(realmRole);
            }
        }
        return realmRoles;
    }

    /**
     * Parses the user's initial client roles from the user identity {@link #userIdentity}
     *
     * @return user's client roles
     * @throws JSONException
     */
    private Set<UserRole> parseClientRoles() throws JSONException {
        Set<UserRole> clientRoles = new HashSet<>();

        if (serviceConfiguration.getProperty(RESOURCE) != null) {
            String initialClientID = serviceConfiguration.getProperty(RESOURCE);  //immediate client role

            if (userIdentity.has(CLIENT) && userIdentity.getJSONObject(CLIENT).has(initialClientID)
                && userIdentity.getJSONObject(CLIENT).getJSONObject(initialClientID).has(ROLES)) {
                String tokenClientRolesJSON = userIdentity.getJSONObject(CLIENT).getJSONObject(initialClientID).getString(ROLES);

                String clientRolesString = tokenClientRolesJSON.substring(1, tokenClientRolesJSON.length() - 1).replace("\"", "");
                String roles[] = clientRolesString.split(COMMA);

                for (String roleName : roles) {
                    UserRole clientRole = new UserRole(roleName, RoleType.CLIENT, initialClientID);
                    clientRoles.add(clientRole);
                }
            }
        }
        return clientRoles;
    }

    /**
     * Gets the user's identity by decoding the user's access token {@link OIDCCredentials#getAccessToken()}
     *
     * @return user's identity
     * @throws JSONException
     * @throws AuthenticationException
     */
    private void decodeUserIdentity() throws JSONException, AuthenticationException {
        String accessToken = ((OIDCCredentials) credential).getAccessToken();

        try {
            // Decode the Access Token to Extract the Identity Information
            String[] splitToken = accessToken.split("\\.");
            byte[] decodedBytes = Base64.decode(splitToken[1], Base64.URL_SAFE);
            String decoded = new String(decodedBytes, "UTF-8");
            try {
                userIdentity = new JSONObject(decoded);
            } catch (JSONException e) {
                throw new AuthenticationException(e.getMessage(), e.getCause());
            }

        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
    }
}
