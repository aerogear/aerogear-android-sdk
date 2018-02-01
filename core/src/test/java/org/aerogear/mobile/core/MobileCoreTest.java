package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.BootstrapException;
import org.aerogear.mobile.core.exception.NotInitializedException;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreTest {

    private String keycloakUrl = "http://keycloak-myproject.192.168.37.1.nip.io/auth";

    @Before
    public void setup() {
        MobileCore.cleanup();
    }

    @Test
    public void testInit() {
        Application context = RuntimeEnvironment.application;

        MobileCore.init(context);

        // -- Config
        ServiceConfiguration kcConfig = MobileCore.getServiceConfiguration("keycloak");

        assertEquals(keycloakUrl, kcConfig.getProperty("auth-server-url"));

        // -- Http
        assertEquals(OkHttpServiceModule.class, MobileCore.getHttpLayer().getClass());
    }

    @Test
    public void testInitWithDifferentHttpServiceModule() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setHttpServiceModule(new DummyHttpServiceModule());

        MobileCore.init(context, options);

        // -- Config
        ServiceConfiguration kcConfig = MobileCore.getServiceConfiguration("keycloak");
        assertEquals(keycloakUrl, kcConfig.getProperty("auth-server-url"));

        // -- Http Layer
        assertEquals(DummyHttpServiceModule.class, MobileCore.getHttpLayer().getClass());
    }

    @Test(expected = BootstrapException.class)
    public void testInitWithWrongConfigFile() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
    }

    @Test(expected = NotInitializedException.class)
    public void testGetHttpLayerBeforeInitialize() {
        MobileCore.getHttpLayer();
    }

    @Test(expected = NotInitializedException.class)
    public void testGetServiceConfigurationBeforeInitialize() {
        MobileCore.getServiceConfiguration("whatever");
    }

    // -- Helpers ---------------------------------------------------------------------------------

    public static final class DummyHttpServiceModule implements HttpServiceModule {

        @Override
        public HttpRequest newRequest() {
            return null;
        }

    }

}
