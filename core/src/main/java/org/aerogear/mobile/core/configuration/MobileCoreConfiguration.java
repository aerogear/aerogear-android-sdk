package org.aerogear.mobile.core.configuration;

import java.util.Collections;
import java.util.Map;

import org.aerogear.mobile.core.configuration.https.HttpsConfiguration;

/**
 * Class for a parsed JSON configuration for the Mobile Core.
 */
public class MobileCoreConfiguration {
    private final HttpsConfiguration httpsConfig;
    private final Map<String, ServiceConfiguration> serviceConfig;

    public MobileCoreConfiguration(final Map<String, ServiceConfiguration> serviceConfig,
                    final HttpsConfiguration httpsConfig) {
        this.httpsConfig = httpsConfig;
        this.serviceConfig = serviceConfig;
    }

    static class Builder {
        private HttpsConfiguration httpsConfig;
        private Map<String, ServiceConfiguration> serviceConfig;

        Builder() {}

        public Builder setServiceConfiguration(
                        final Map<String, ServiceConfiguration> serviceConfig) {
            this.serviceConfig = serviceConfig;
            return this;
        }

        public Builder setHttpsConfiguration(final HttpsConfiguration httpsConfig) {
            this.httpsConfig = httpsConfig;
            return this;
        }

        public MobileCoreConfiguration build() {
            return new MobileCoreConfiguration(serviceConfig, httpsConfig);
        }
    }

    /**
     * Get the services configuration.
     * 
     * @return Services details from mobile core config.
     */
    public Map<String, ServiceConfiguration> getServicesConfig() {
        return Collections.unmodifiableMap(serviceConfig);
    }

    /**
     * Get the https configuration.
     * 
     * @return {@link HttpsConfiguration}
     */
    public HttpsConfiguration getHttpsConfig() {
        return httpsConfig;
    }

    /**
     * Return a new {@link MobileCoreConfiguration.Builder}.
     * 
     * @return new {@link MobileCoreConfiguration.Builder}.
     */
    public static Builder newBuilder() {
        return new Builder();
    }
}
