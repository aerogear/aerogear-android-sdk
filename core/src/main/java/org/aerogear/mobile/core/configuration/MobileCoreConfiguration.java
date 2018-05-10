package org.aerogear.mobile.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aerogear.mobile.core.configuration.https.HttpsConfiguration;

/**
 * Class for a parsed JSON configuration for the Mobile Core.
 */
public class MobileCoreConfiguration {
    private final HttpsConfiguration httpsConfig;

    // a map of <service id, config>
    private final Map<String, ServiceConfiguration> serviceConfigsPerId;

    // a map of <service type, list<config>> as there can be multiple configs for a service type
    private final Map<String, List<ServiceConfiguration>> serviceConfigsByType;

    public MobileCoreConfiguration(final Map<String, ServiceConfiguration> serviceConfigsPerId,
                    final HttpsConfiguration httpsConfig) {
        this.httpsConfig = httpsConfig;
        this.serviceConfigsPerId = new HashMap<>(serviceConfigsPerId);

        // now, create a map of <service type, list<config>> from <service id, config> map
        serviceConfigsByType = new HashMap<>();

        for (ServiceConfiguration serviceConfiguration : serviceConfigsPerId.values()) {
            final String serviceType = serviceConfiguration.getType();
            List<ServiceConfiguration> configsForType = serviceConfigsByType.get(serviceType);
            if (configsForType == null) {
                configsForType = new ArrayList<>();
                serviceConfigsByType.put(serviceType, configsForType);
            }
            configsForType.add(serviceConfiguration);
        }
    }

    public static class Builder {
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
     * Get the services configuration. Map of service ids to service configurations.
     *
     * @return Services details from mobile core config.
     */
    public Map<String, ServiceConfiguration> getServicesConfigPerId() {
        return Collections.unmodifiableMap(serviceConfigsPerId);
    }

    public Map<String, List<ServiceConfiguration>> getServiceConfigsPerType() {
        return Collections.unmodifiableMap(serviceConfigsByType);
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
