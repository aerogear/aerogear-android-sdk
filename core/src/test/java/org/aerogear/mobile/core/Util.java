package org.aerogear.mobile.core;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

/**
 * This is for various test structures and utility methods that will be used in the core tests.
 */
public class Util {
    public  static class StubServiceModule implements ServiceModule {
        public  StubServiceModule() {}
        @Override
        public void bootstrap(MobileCore core, ServiceConfiguration configuration) {

        }
    }

    public  static class StubServiceModule2 implements ServiceModule {
        public StubServiceModule service1;
        public ServiceConfiguration config;

        public  StubServiceModule2() {}
        @Override
        public void bootstrap(MobileCore core, ServiceConfiguration configuration) {

            service1 = (StubServiceModule) core.getService("prometheus");
            config = configuration;
        }
    }


    public static ServiceModuleRegistry getDefaultRegistry() {
        ServiceModuleRegistry defaultRegistry = new ServiceModuleRegistry();
        defaultRegistry.registerServiceModule("fh-sync-server", StubServiceModule.class);
        defaultRegistry.registerServiceModule("keycloak", StubServiceModule.class);
        defaultRegistry.registerServiceModule("prometheus", StubServiceModule.class);
        defaultRegistry.registerServiceModule("unified-push-server", StubServiceModule.class);
        return defaultRegistry;
    }

}
