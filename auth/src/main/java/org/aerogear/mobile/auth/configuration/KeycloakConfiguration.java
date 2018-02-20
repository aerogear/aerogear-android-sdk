package org.aerogear.mobile.auth.configuration;

import android.net.Uri;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;
import static org.aerogear.mobile.core.utils.SanityCheck.nonEmpty;

/**
 * A class to represent the configuration options of the Keycloak service
 */
public class KeycloakConfiguration {

    private static final String SERVER_URL_NAME = "auth-server-url";
    private static final String REALM_ID_NAME = "realm";
    private static final String CLIENT_ID_NAME = "resource";

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private static final String BASE_URL_TEMPLATE = "%s/realms/%s/protocol/openid-connect";
    private static final String LOGOUT_URL_TEMPLATE = "%s/logout?%s=%s&%s=%s";

    private final String serverUrl;
    private final String realmId;
    private final String clientId;
    private final String baseUrl;

    /**
     * Create a new instance of the Keycloak configuration from the given instance of ServiceConfiguration.
     * @param configuration the ServiceConfiguration instance for Keycloak
     */
    public KeycloakConfiguration(final ServiceConfiguration configuration) {
        nonNull(configuration, "configuration");

        this.serverUrl = nonEmpty(configuration.getProperty(SERVER_URL_NAME), SERVER_URL_NAME);
        this.realmId = nonEmpty(configuration.getProperty(REALM_ID_NAME), REALM_ID_NAME);
        this.clientId = nonEmpty(configuration.getProperty(CLIENT_ID_NAME), CLIENT_ID_NAME);
        this.baseUrl = String.format(BASE_URL_TEMPLATE, serverUrl, realmId);
    }

    /**
     * Get the URI for the Keycloak authentication endpoint
     * @return the authentication endpoint URI
     */
    public Uri getAuthenticationEndpoint() {
        return Uri.parse(this.baseUrl + "/auth");
    }

    /**
     * Get the URI for the token exchange endpoint
     * @return the token exchange endpoint URI
     */
    public Uri getTokenEndpoint() {
        return Uri.parse( this.baseUrl + "/token");
    }

    /**
     * Get the client id
     * @return the client id
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Get the logout URL string
     * @param identityToken the identity token
     * @param redirectUri the redirect uri
     * @return the full logout URL string
     */
    public String getLogoutUrl(final String identityToken, final String redirectUri) {
        return String.format(LOGOUT_URL_TEMPLATE, this.baseUrl, TOKEN_HINT_FRAGMENT, identityToken, REDIRECT_FRAGMENT, redirectUri);
    }

    /**
     * Get the URL string of the Keycloak service
     * @return the URL of the Keycloak service
     */
    public String getHostUrl() {
        return this.serverUrl;
    }

    /**
     * Get the realm name of the Keycloak service
     * @return the realm name
     */
    public String getRealmName() {
        return this.realmId;
    }

    public String getJwksUrl() {
        return this.baseUrl + "/certs";
    }
}
