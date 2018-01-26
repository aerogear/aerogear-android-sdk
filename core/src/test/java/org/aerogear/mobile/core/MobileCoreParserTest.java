package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.junit.Assert;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import static org.aerogear.mobile.core.Util.getDefaultRegistry;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreParserTest {

    @Test(expected = BootstrapException.class)
    public void testUndefinedServiceThrowsExcption() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application).setMobileServiceFileName("undefined-mobile-services.json");
        try {
            MobileCore core = builder.build();
        } catch (BootstrapException exception) {
            assertEquals("Service with name fh-sync-server does not have a type in the ServiceRegistry.", exception.getMessage());
            throw exception;
        }


    }

    @Test
    public void testAssetMobileCoreParsing() throws IOException {
        Application application = RuntimeEnvironment.application;

        MobileCore.Builder builder = new MobileCore.Builder(application)
                                            .setServiceRegistry(getDefaultRegistry());

        MobileCore core = builder.build();
        Assert.assertNotNull(core.getConfig("prometheus"));
        ServiceConfiguration config = core.getConfig("keycloak");
        assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", config.getProperty("auth-server-url"));
        assertEquals("null", core.getConfig("null").getName());

    }

    @Test
    public void testConfigurableMobileServiceFileName() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application)
                                            .setMobileServiceFileName("mobile-core.json")
                                            .setServiceRegistry(getDefaultRegistry());
        MobileCore core = builder.build();
        Assert.assertNotNull(core.getConfig("prometheus"));
        ServiceConfiguration config = core.getConfig("keycloak");
        assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", config.getProperty("auth-server-url"));
        assertEquals("null", core.getConfig("null").getName());
    }

    @Test(expected = BootstrapException.class)
    public void testMobileConfigBootstrapExceptionFileNotFound() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application)
                .setMobileServiceFileName("mobile-co2re.json");
        try {
            MobileCore core = builder.build();
        } catch (BootstrapException exception) {
            assertEquals("mobile-co2re.json could not be loaded", exception.getMessage());
            throw exception;
        }

    }

}

