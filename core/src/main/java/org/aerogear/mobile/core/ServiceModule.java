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
     * Called when service destroyed
     */
    void destroy();

}
