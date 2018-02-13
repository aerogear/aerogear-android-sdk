package org.aerogear.mobile.auth.configuration;

import android.net.Uri;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public class AuthenticationConfiguration {

    private static final String SERVER_URL_NAME = "auth-server-url";
    private static final String REALM_ID_NAME = "realm";
    private static final String CLIENT_ID_NAME = "clientId";

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private static final String BASE_URL_TEMPLATE = "%s/realms/%s/protocol/openid-connect";
    private static final String LOGOUT_URL_TEMPLATE = "%s/logout?%s=%s&%s=%s";

    private final String serverUrl;
    private final String realmId;
    private final String clientId;
    private final String baseUrl;

    public AuthenticationConfiguration(ServiceConfiguration configuration) {
        this.serverUrl = configuration.getProperty(SERVER_URL_NAME);
        this.realmId = configuration.getProperty(REALM_ID_NAME);
        this.clientId = configuration.getProperty(CLIENT_ID_NAME);
        this.baseUrl = String.format(BASE_URL_TEMPLATE, serverUrl, realmId);
    }

    public Uri getAuthenticationEndpoint() {
        return Uri.parse(this.baseUrl + "/auth");
    }

    public Uri getTokenEndpoint() {
        return Uri.parse( this.baseUrl + "/token");
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getLogoutUrl(String identityToken, String redirectUri) {
        return String.format(LOGOUT_URL_TEMPLATE, this.baseUrl, TOKEN_HINT_FRAGMENT, identityToken, REDIRECT_FRAGMENT, redirectUri);
    }

    public String getHostUrl() {
        return this.serverUrl;
    }
}
