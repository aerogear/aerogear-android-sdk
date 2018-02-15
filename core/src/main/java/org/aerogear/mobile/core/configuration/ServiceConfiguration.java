package org.aerogear.mobile.core.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a parsed service configuration from JSON configuration.
 */
public final class ServiceConfiguration {

    private final String name;
    private final String type;
    private final String url;
    private final HashMap<String, String> properties;

    private ServiceConfiguration(final String name,
                                 final HashMap<String, String> properties,
                                 final String type,
                                 final String url) {
        this.name = name;
        this.properties = properties;
        this.type = type;
        this.url = url;
    }

    public static class Builder {

        protected String name;
        protected HashMap<String, String> properties = new HashMap<>();
        protected String type;
        protected String uri;

        public Builder setName(final String name) {
            this.name = name;
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
            return new ServiceConfiguration(this.name, this.properties, this.type, this.uri);
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

    public String getUrl() {
        return url;
    }

    public static Builder newConfiguration() {
        return new Builder();
    }
}
