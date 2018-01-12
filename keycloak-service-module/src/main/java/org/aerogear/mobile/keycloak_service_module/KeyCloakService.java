package org.aerogear.mobile.keycloak_service_module;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.JsonReader;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class KeyCloakService implements ServiceModule {

    private final Context appContext;
    private KeyCloakConfig config;
    private String serverUrl;
    private String clientId;
    private String audience;
    private String grantType;
    private String subjectTokenType;
    private String requestedTokenType;
    private String realm;
    private MobileCore core;

    public KeyCloakService(@NonNull Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    /**
     * Exchanges the google id token and configures the KeyCloakService to serve requests
     *
     * @param token a Google ID token
     */
    public void login(String token) {

    }

    @Override
    public void bootstrap(MobileCore core, ServiceConfiguration config, Object... args) {
        this.serverUrl = config.getProperty("auth-server-url");
        this.clientId = config.getProperty("client_id");
        this.audience = config.getProperty("audience");
        this.grantType = config.getProperty("grant_type");
        this.subjectTokenType = config.getProperty("subject_token_type");
        this.requestedTokenType = config.getProperty("requested_token_type");
        this.realm = config.getProperty("realm");
        this.core = core;
    }
}
