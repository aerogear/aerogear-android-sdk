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

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreParserTest {

    @Test
    public void testAssetMobileCoreParsing() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application);
        MobileCore core = builder.build();
        Assert.assertNotNull(core.getConfig("prometheus"));
        ServiceConfiguration config = core.getConfig("keycloak");
        Assert.assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", config.getProperty("auth-server-url"));
        Assert.assertEquals("null", core.getConfig("null").getName());

    }

    @Test
    public void testConfigurableMobileServiceFileName() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application)
                .setMobileServiceFileName("mobile-core.json");
        MobileCore core = builder.build();
        Assert.assertNotNull(core.getConfig("prometheus"));
        ServiceConfiguration config = core.getConfig("keycloak");
        Assert.assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", config.getProperty("auth-server-url"));
        Assert.assertEquals("null", core.getConfig("null").getName());
    }

    @Test(expected = BootstrapException.class)
    public void testMobileConfigBootstrapExceptionFileNotFound() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application)
                .setMobileServiceFileName("mobile-co2re.json");
        try {
            MobileCore core = builder.build();
        } catch (BootstrapException exception) {
            org.junit.Assert.assertEquals("mobile-co2re.json could not be loaded", exception.getMessage());
            throw exception;
        }

    }

}

