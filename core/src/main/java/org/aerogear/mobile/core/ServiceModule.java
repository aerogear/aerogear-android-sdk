package org.aerogear.mobile.core;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;


public interface ServiceModule {

    /**
     * This is the method called by mobile-core to startup modules.
     * @param core an instance of mobile core
     * @param configuration service configuration for this instance of a service module
     * @param args any data that a module would need to start up
     * @throws BootstrapException if bootstrapping fails.
     */
    void bootstrap(MobileCore core, ServiceConfiguration configuration, Object... args);
}
