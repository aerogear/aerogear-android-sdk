package org.aerogear.mobile.keycloak_service_module;

import android.content.Context;
import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.ServiceModuleRegistry;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;

import java.io.IOException;

import okhttp3.FormBody;
import okio.Buffer;
import okio.BufferedSink;


public class KeyCloakService implements ServiceModule {

    private KeyCloakConfig config;
    private String serverUrl;
    private String clientId;
    private String audience;
    private String grantType;
    private String subjectTokenType;
    private String requestedTokenType;
    private String realm;
    private MobileCore core;
    private String resource;
    private HttpServiceModule httpModule;
    private String accessToken;


    public KeyCloakService() {
    }


    @Override
    public void bootstrap(MobileCore core, ServiceConfiguration config, Object... args) {
        this.serverUrl = config.getProperty("auth-server-url");
        this.clientId = config.getProperty("clientId");
        this.audience = config.getProperty("audience");
        this.grantType = config.getProperty("grant_type");
        this.subjectTokenType = config.getProperty("subject_token_type");
        this.requestedTokenType = config.getProperty("requested_token_type");
        this.resource = config.getProperty("resource");
        this.realm = config.getProperty("realm");
        this.core = core;
        this.httpModule = (HttpServiceModule) core.getService("http");
    }


    /**
     * Exchanges the google id token and configures the KeyCloakService to serve requests
     *
     * @param googleIdToken a Google ID token
     * @return a {@link HttpResponse}
     */
    public HttpResponse exchangeToken(String googleIdToken) {
        HttpRequest request = httpModule.newRequest();
        request.addHeader(HttpRequest.CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded");

        FormBody requestBody = new FormBody.Builder()
            .add("client_id", clientId)
            .add("audience", clientId)
            .add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
            .add("subject_token", googleIdToken)
            .add("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
            .add("requested_token_type", "urn:ietf:params:oauth:token-type:refresh_token")
            .build();

        BufferedSink sink = new Buffer();
        try {
            requestBody.writeTo(sink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        request.post(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token", sink.buffer().readByteArray());

        return request.execute();

    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;

    }

    public void addBearerToken(HttpRequest request) {
        if (accessToken != null) {
            request.addHeader("Authorization", "Bearer " + accessToken);
        }
    }

}
