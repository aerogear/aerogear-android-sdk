package org.aerogear.mobile.keycloak_service_module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.net.URL;

public class KeyCloakConfig {

    private String realm = "";
    private URL authServerUrl = null;
    private String sslRequired = "";
    private String resource = "";
    private boolean publicClient = false;
    private int confidentialPort = 0;

    /**
     * Parses a JSON reader and returns the KeyCloakConfig it represents.  Unknown values are ignored.
     *
     * @param reader a jsonreader for a keycloak.json file set at the beginning of the file.
     * @return a KeyCloakConfig, never null.
     * @throws IOException if the reader throws an exception.
     */
    public static KeyCloakConfig parse(JsonReader reader) throws IOException {
        KeyCloakConfig config = new KeyCloakConfig();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token == JsonToken.NAME) {
                String name = reader.nextName();
                switch (name) {
                    case "realm":
                        config.realm = reader.nextString();
                        break;
                    case "auth-server-url":
                        config.authServerUrl = new URL(reader.nextString());
                        break;
                    case "ssl-required":
                        config.sslRequired = reader.nextString();
                        break;
                    case "resource":
                        config.resource = reader.nextString();
                        break;
                    case "public-client":
                        config.publicClient = reader.nextBoolean();
                        break;
                    case "confidential-port":
                        config.confidentialPort = reader.nextInt();
                        break;
                    default:
                        reader.skipValue();
                }
            }
        }
        return config;
    }

    @NonNull
    public String getRealm() {
        return realm;
    }

    @Nullable
    public URL getAuthServerUrl() {
        return authServerUrl;
    }

    @NonNull
    public String getSslRequired() {
        return sslRequired;
    }

    @NonNull
    public String getResource() {
        return resource;
    }

    public boolean isPublicClient() {
        return publicClient;
    }

    public int getConfidentialPort() {
        return confidentialPort;
    }
}
