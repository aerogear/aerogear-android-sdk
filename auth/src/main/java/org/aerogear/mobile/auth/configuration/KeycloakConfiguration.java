package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonEmpty;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.net.Uri;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

/**
 * A class to represent the configuration options of the Keycloak singleThreadService
 */
public class KeycloakConfiguration {

    private static final String AUTH_SERVER_URL_NAME = "auth-server-url";
    private static final String SERVER_URL_NAME = "url";
    private static final String REALM_ID_NAME = "realm";
    private static final String RESOURCE_ID_NAME = "resource";

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private static final String BASE_URL_TEMPLATE = "%s/realms/%s/protocol/openid-connect";
    private static final String LOGOUT_URL_TEMPLATE = "%s/logout?%s=%s&%s=%s";

    private final String authServerUrl;
    private final String serverUrl;
    private final String realmId;
    private final String resourceId;
    private final String baseUrl;

    /**
     * Create a new instance of the Keycloak configuration from the given instance of
     * ServiceConfiguration.
     *
     * @param configuration the ServiceConfiguration instance for Keycloak
     */
    public KeycloakConfiguration(final ServiceConfiguration configuration) {
        nonNull(configuration, "configuration");

        this.authServerUrl = nonEmpty(configuration.getProperty(AUTH_SERVER_URL_NAME),
                        AUTH_SERVER_URL_NAME);
        this.serverUrl = nonEmpty(configuration.getUrl(), SERVER_URL_NAME);
        this.realmId = nonEmpty(configuration.getProperty(REALM_ID_NAME), REALM_ID_NAME);
        this.resourceId = nonEmpty(configuration.getProperty(RESOURCE_ID_NAME), RESOURCE_ID_NAME);
        this.baseUrl = String.format(BASE_URL_TEMPLATE, serverUrl, realmId);
    }

    /**
     * Get the URI for the Keycloak authentication endpoint
     *
     * @return the authentication endpoint URI
     */
    public Uri getAuthenticationEndpoint() {
        return Uri.parse(this.baseUrl + "/auth");
    }

    /**
     * Get the URI for the token exchange endpoint
     *
     * @return the token exchange endpoint URI
     */
    public Uri getTokenEndpoint() {
        return Uri.parse(this.baseUrl + "/token");
    }

    /**
     * Get the resource id
     *
     * @return the resource id
     */
    public String getResourceId() {
        return this.resourceId;
    }

    /**
     * Get the logout URL string
     *
     * @param identityToken the identity token
     * @param redirectUri the redirect uri
     * @return the full logout URL string
     */
    public String getLogoutUrl(final String identityToken, final String redirectUri) {
        return String.format(LOGOUT_URL_TEMPLATE, this.baseUrl, TOKEN_HINT_FRAGMENT, identityToken,
                        REDIRECT_FRAGMENT, redirectUri);
    }

    /**
     * Get the URL string of the Keycloak singleThreadService
     *
     * @return the URL of the Keycloak singleThreadService
     */
    public String getHostUrl() {
        return this.serverUrl;
    }

    /**
     * Get the Auth Server URL string of the Keycloak singleThreadService This URL should only be
     * used to get the issuer field for token verification.
     *
     * @return the Issuer Hostname of the Keycloak Server
     */
    public String getIssuerHostname() {
        return this.authServerUrl;
    }

    /**
     * Get the realm name of the Keycloak singleThreadService
     *
     * @return the realm name
     */
    public String getRealmName() {
        return this.realmId;
    }

    /**
     * Returns the URL where keys can be retrieved.
     *
     * @return the URL where keys can be retrieved.
     */
    public String getJwksUrl() {
        return this.baseUrl + "/certs";
    }

    /**
     * Returns the JWT Issuer
     *
     * @return the JWT Issuer
     */
    public String getIssuer() {
        return String.format("%s/realms/%s", getIssuerHostname(), getRealmName());
    }
}
