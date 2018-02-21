package org.aerogear.mobile.core;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public interface ServiceModule {
    /**
     * Type/name used in the mobile-service.json
     *
     * @return return the type/name used to identify the service config in the JSON file
     */
    String type();

    /**
     * A method how create/configure the service
     *
     * @param core MobileCore instance
     * @param serviceConfiguration the configuration for the service
     */
    void configure(MobileCore core, ServiceConfiguration serviceConfiguration);

    /**
     * Whether the service module requires its service configuration to be defined or if it can be
     * null. If this is <code>true</code> then an exception will be thrown if service configuration
     * cannot be found.
     *
     * @return <code>true</code> if the service configuration should be defined.
     */
    boolean requiresConfiguration();

    /**
     * Called when service destroyed
     */
    void destroy();

}
