package org.aerogear.mobile.core;

public interface ServiceModule {

    /**
     * This is the method called by mobile-core to startup modules.
     * @param args any data that a module would need to start up
     * @throws BootstrapException if bootstrapping fails.
     */
    void bootstrap(Object... args);
}
