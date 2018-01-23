package org.aerogear.android.ags.auth.impl;

import android.util.Base64;

import org.aerogear.auth.AuthServiceConfig;
import org.aerogear.auth.AuthenticationException;
import org.aerogear.auth.ClientRole;
import org.aerogear.auth.IRole;
import org.aerogear.auth.RealmRole;
import org.aerogear.auth.RoleKey;
import org.aerogear.auth.credentials.ICredential;
import org.aerogear.auth.credentials.OIDCCredentials;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthCodeImpl extends OIDCTokenAuthenticatorImpl {
    
    private static final String USERNAME = "preferred_username";
    private static final String EMAIL = "email";
    private static final String REALM = "realm_access";
    private static final String CLIENT = "resource_access";
    private static final String ROLES = "roles";
    private static final String RESOURCE = "resource";
    private static final String COMMA = ",";

    private JSONObject userIdentity = new JSONObject();

    /**
     * Creates a new OIDCAuthCodeImpl object
     *
     * @param config the authentication service configuration
     */
    public OIDCAuthCodeImpl(final ServiceConfiguration serviceConfig) {
        super(serviceConfig);
    }


    /**
     * Builds a new OIDCUserPrincipalImpl object after the user's credential has been authenticated
     *
     * @param credential the OIDC credential for the user
     * @return a new OIDCUserPrincipalImpl object with the user's identity {@link #userIdentity} that was decoded from the user's credential
     * @throws AuthenticationException
     * @see OIDCTokenAuthenticatorImpl#authenticate(ICredential)
     */
    @Override
    public Principal authenticate(final ICredential credential) throws AuthenticationException {
        OIDCUserPrincipalImpl user;
        try {
            userIdentity = getIdentityInformation(credential);
            user = (OIDCUserPrincipalImpl) OIDCUserPrincipalImpl
                .newUser()
                .withAuthenticator(this)
                .withUsername(parseUsername())
                .withCredentials(credential)
                .withEmail(parseEmail())
                .withRoles(parseRoles())
                .build();
        } catch (JSONException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
    return user;
    }

    /**
     * Parses the user's username from the user identity {@link #userIdentity}
     *
     * @return user's username
     * @throws JSONException
     */
    private String parseUsername() throws JSONException {
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
    private String parseEmail() throws JSONException {
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
    private Collection<IRole> parseRoles() throws JSONException {
        Collection<IRole> roles = new ArrayList<IRole>();
        if (userIdentity != null) {
            Map<RoleKey, IRole> realmRoles = parseRealmRoles();
            if (realmRoles != null) {
                roles.addAll(realmRoles.values());
            }
            Map<RoleKey, IRole> clientRoles = parseClientRoles();
            if (clientRoles != null) {
                roles.addAll(clientRoles.values());
            }
        }
        return roles;
    }

    /**
     * Parses the user's realm roles from the user identity {@link #userIdentity}
     *
     * @return user's realm roles
     * @throws JSONException
     * @see RoleKey
     * @see RealmRole
     */
    private Map<RoleKey, IRole> parseRealmRoles() throws JSONException {
        Map<RoleKey, IRole> realmRoles = new HashMap<>();
        if (userIdentity.has(REALM) && userIdentity.getJSONObject(REALM).has(ROLES)) {
            String tokenRealmRolesJSON = userIdentity.getJSONObject(REALM).getString(ROLES);

            String realmRolesString = tokenRealmRolesJSON.substring(1, tokenRealmRolesJSON.length() - 1).replace("\"", "");
            String roles[] = realmRolesString.split(COMMA);

            for (String rolename : roles) {
                RealmRole realmRole = new RealmRole(rolename);
                realmRoles.put(new RoleKey(realmRole, null), realmRole);
            }
        }
        return realmRoles;
    }

    /**
     * Parses the user's initial client roles from the user identity {@link #userIdentity}
     *
     * @return user's client roles
     * @throws JSONException
     * @see AuthServiceConfig for initial client
     * @see RoleKey
     * @see ClientRole
     */
    private Map<RoleKey, IRole> parseClientRoles() throws JSONException {
        Map<RoleKey, IRole> clientRoles = new HashMap<>();

        AuthServiceConfig authConfig = this.getConfig();
        JSONObject authJSON =  authConfig.toJSON();

        if (authJSON.has(RESOURCE)) {
            String initialClientID = authJSON.get(RESOURCE).toString();  //immediate client role

            if (userIdentity.has(CLIENT) && userIdentity.getJSONObject(CLIENT).has(initialClientID)
                    && userIdentity.getJSONObject(CLIENT).getJSONObject(initialClientID).has(ROLES)) {
                String tokenClientRolesJSON = userIdentity.getJSONObject(CLIENT).getJSONObject(initialClientID).getString(ROLES);

                String clientRolesString = tokenClientRolesJSON.substring(1, tokenClientRolesJSON.length() - 1).replace("\"", "");
                String roles[] = clientRolesString.split(COMMA);

                for (String rolename : roles) {
                    ClientRole clientRole = new ClientRole(rolename, initialClientID);
                    clientRoles.put(new RoleKey(clientRole, initialClientID), clientRole);
                }
            }
        }
        return clientRoles;
    }

    /**
     * Gets the user's identity by decoding the user's access token {@link OIDCCredentials#getAccessToken()}
     *
     * @param credential
     * @return user's identity
     * @throws JSONException
     * @throws AuthenticationException
     */
    private JSONObject getIdentityInformation(final ICredential credential) throws JSONException, AuthenticationException {
        String accessToken = ((OIDCCredentials) credential).getAccessToken();
        JSONObject decodedIdentityData = new JSONObject();

        try {
            // Decode the Access Token to Extract the Identity Information
            String[] splitToken = accessToken.split("\\.");
            byte[] decodedBytes = Base64.decode(splitToken[1], Base64.URL_SAFE);
            String decoded = new String(decodedBytes, "UTF-8");
            try {
                decodedIdentityData = new JSONObject(decoded);
            } catch (JSONException e) {
                throw new AuthenticationException(e.getMessage(), e.getCause());
            }

        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
        return decodedIdentityData;
    }
}
