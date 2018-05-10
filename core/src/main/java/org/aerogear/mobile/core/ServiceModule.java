package org.aerogear.mobile.core;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public interface ServiceModule {
    /**
     * Type/name used in the mobile-singleThreadService.json
     *
     * @return return the type/name used to identify the singleThreadService config in the JSON file
     */
    String type();

    /**
     * A method how create/configure the singleThreadService
     *
     * @param core MobileCore instance
     * @param serviceConfiguration the configuration for the singleThreadService
     */
    void configure(MobileCore core, ServiceConfiguration serviceConfiguration);

    /**
     * Whether the singleThreadService module requires its singleThreadService configuration to be
     * defined or if it can be null. If this is <code>true</code> then an exception will be thrown
     * if singleThreadService configuration cannot be found.
     *
     * @return <code>true</code> if the singleThreadService configuration should be defined.
     */
    boolean requiresConfiguration();
}
