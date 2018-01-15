package org.aerogear.mobile.core;


import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class ServiceRegistryTest {

    private Application application;

    @Before
    public void parseMobileCore() {
        this.application = RuntimeEnvironment.application;

    }

    @Test()
    public void testSimpleServiceInit() {
        ServiceModuleRegistry registry = new ServiceModuleRegistry();
        registry.registerServiceModule("simpleService", StubServiceModule.class);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setRegistryService(registry);
        MobileCore core = builder.build();
        assertNotNull(core.getService("simpleService"));

    }

    @Test(expected = BootstrapException.class)
    public void testCircularDependenciesAreCaughtAndExceptionIsThrown() {
        ServiceModuleRegistry registry = new ServiceModuleRegistry();
        registry.registerServiceModule("crashService", StubServiceModule.class, "crashService");

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setRegistryService(registry);
        try {
            MobileCore core = builder.build();

        } catch (BootstrapException ex) {
            assertEquals("Unresolvable service detected crashService", ex.getMessage());
            throw ex;
        }

    }


    @Test
    public void testDependenciesAreResolvedInOrder() {
        ServiceModuleRegistry registry = new ServiceModuleRegistry();
        registry.registerServiceModule("service1", StubServiceModule.class);
        registry.registerServiceModule("service2", StubServiceModule2.class, "service1");

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setRegistryService(registry);

        MobileCore core = builder.build();
        assertNotNull(core.getService("service1"));
        assertNotNull(core.getService("service2"));
        assertNotNull(((StubServiceModule2)core.getService("service2")).service1);

    }


    @Test
    public void testConfigurationIsPassedFromParsedFile() {
        ServiceModuleRegistry registry = new ServiceModuleRegistry();
        registry.registerServiceModule("prometheus", StubServiceModule2.class);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setRegistryService(registry);

        MobileCore core = builder.build();
        assertNotNull(core.getService("prometheus"));
        assertEquals("https://prometheus-myproject.192.168.37.1.nip.io", ((StubServiceModule2)core.getService("prometheus")).config.getUri());

    }

    public  static class StubServiceModule implements ServiceModule {
        public  StubServiceModule() {}
        @Override
        public void bootstrap(MobileCore core, ServiceConfiguration configuration, Object... args) {

        }
    }

    public  static class StubServiceModule2 implements ServiceModule {
        public StubServiceModule service1;
        public ServiceConfiguration config;

        public  StubServiceModule2() {}
        @Override
        public void bootstrap(MobileCore core, ServiceConfiguration configuration, Object... args) {

            service1 = (StubServiceModule) core.getService("service1");
            config = configuration;
        }
    }

}
