package org.aerogear.mobile.core;


import android.app.Application;
import android.support.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.aerogear.mobile.core.Util.getDefaultRegistry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.aerogear.mobile.core.Util.StubServiceModule;
import org.aerogear.mobile.core.Util.StubServiceModule2;

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
        ServiceModuleRegistry registry = getDefaultRegistry();
        registry.registerServiceModule("keycloak", StubServiceModule.class);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);
        MobileCore core = builder.build();
        assertNotNull(core.getService("keycloak"));

    }

    @Test(expected = BootstrapException.class)
    public void testCircularDependenciesAreCaughtAndExceptionIsThrown() {
        ServiceModuleRegistry registry = getDefaultRegistry();
        registry.registerServiceModule("prometheus", StubServiceModule.class, "crashService");

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);
        try {
            MobileCore core = builder.build();

        } catch (BootstrapException ex) {
            assertEquals("Unresolvable service detected prometheus", ex.getMessage());
            throw ex;
        }

    }


    @Test
    public void testDependenciesAreResolvedInOrder() {
        ServiceModuleRegistry registry = getDefaultRegistry();
        registry.registerServiceModule("prometheus", StubServiceModule.class);
        registry.registerServiceModule("keycloak", StubServiceModule2.class, "prometheus");

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);

        MobileCore core = builder.build();
        assertNotNull(core.getService("prometheus"));
        assertNotNull(core.getService("keycloak"));
        assertNotNull(((StubServiceModule2)core.getService("keycloak")).service1);

    }

    @Test
    public void testConfigurationIsPassedFromParsedFile() {
        ServiceModuleRegistry registry = getDefaultRegistry();
        registry.registerServiceModule("prometheus", StubServiceModule2.class);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);

        MobileCore core = builder.build();
        assertNotNull(core.getService("prometheus"));
        assertEquals("https://prometheus-myproject.192.168.37.1.nip.io", ((StubServiceModule2)core.getService("prometheus")).config.getUri());

    }

    @Test
    public void testFetchServiceInstance() {
        ServiceModuleRegistry registry = getDefaultRegistry();
        StubServiceModule2 promethusTestInstance = new StubServiceModule2();
        registry.registerServiceModule("prometheus", promethusTestInstance);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);

        MobileCore core = builder.build();
        assertTrue(promethusTestInstance == core.getService("prometheus"));//Test it is the same instance
        assertEquals("https://prometheus-myproject.192.168.37.1.nip.io", ((StubServiceModule2)core.getService("prometheus")).config.getUri());

    }

    @Test
    public void testServiceInstanceIsPreferredOverClass() {
        ServiceModuleRegistry registry = getDefaultRegistry();
        StubServiceModule2 promethusTestInstance = new StubServiceModule2();
        registry.registerServiceModule("prometheus", StubServiceModule.class);
        registry.registerServiceModule("prometheus", promethusTestInstance);

        MobileCore.Builder builder = new MobileCore.Builder(application);
        builder.setServiceRegistry(registry);

        MobileCore core = builder.build();
        assertTrue( core.getService("prometheus") instanceof StubServiceModule2);//Test it is the same instance


    }



}
