package org.aerogear.mobile.core;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public interface ServiceModule {

    /**
     * Type/name used in the mobile-service.json
     *
     * @return
     */
    String type();

    /**
     * A method how create/configure the service
     *
     * @param serviceConfiguration
     */
    void configure(ServiceConfiguration serviceConfiguration);

    /**
     * Called when service destroyed
     */
    void destroy();

}
