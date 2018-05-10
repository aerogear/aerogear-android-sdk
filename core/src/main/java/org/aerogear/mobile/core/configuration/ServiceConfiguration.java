package org.aerogear.mobile.core.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a parsed singleThreadService configuration from JSON configuration.
 */
public final class ServiceConfiguration {

    private final String id;
    private final String type;
    private final String url;
    private final Map<String, String> properties;

    private ServiceConfiguration(final String id, final Map<String, String> properties,
                    final String type, final String url) {
        this.id = id;
        this.properties = properties;
        this.type = type;
        this.url = url;
    }

    public static class Builder {

        protected String id;
        protected Map<String, String> properties = new HashMap<>();
        protected String type;
        protected String uri;

        public Builder setId(final String id) {
            this.id = id;
            return this;
        }

        public Builder addProperty(final String name, final String value) {
            this.properties.put(name, value);
            return this;
        }

        public Builder setType(final String type) {
            this.type = type;
            return this;
        }

        public Builder setUrl(final String uri) {
            this.uri = uri;
            return this;
        }


        public ServiceConfiguration build() {
            return new ServiceConfiguration(this.id, this.properties, this.type, this.uri);
        }
    }

    public String getId() {
        return id;
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

    public String getUrl() {
        return url;
    }

    public static Builder newConfiguration() {
        return new Builder();
    }
}
