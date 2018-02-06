package org.aerogear.mobile.core.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a parsed service from the mobilce-config.json
 */
public final class ServiceConfiguration {
    private final String name;
    private final HashMap<String, String> properties;
    private final String type;
    private final String uri;
    private final HashMap<String, String> headers;

    private ServiceConfiguration(String name, HashMap<String, String> properties, String type, String uri, HashMap<String, String> headers) {
        this.name = name;
        this.properties = properties;
        this.type = type;
        this.uri = uri;
        this.headers = headers;
    }

    public static class Builder {

        protected String name;
        protected HashMap<String, String> properties = new HashMap<>();
        protected String type;
        protected String uri;
        protected HashMap<String, String> headers = new HashMap<>();

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addProperty(String name, String value) {
            this.properties.put(name, value);
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public ServiceConfiguration build() {
            return new ServiceConfiguration(this.name, this.properties, this.type, this.uri, this.headers);
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public static Builder newConfiguration() {
        return new Builder();
    }
}
