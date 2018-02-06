package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreTest {

    @Test
    public void testInit() {
        Application context = RuntimeEnvironment.application;

        MobileCore core = MobileCore.init(context);

        // -- Http
        assertEquals(OkHttpServiceModule.class, core.getHttpLayer().getClass());
    }

    @Test
    public void testInitWithDifferentHttpServiceModule() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setHttpServiceModule(new DummyHttpServiceModule());

        MobileCore core = MobileCore.init(context, options);

        // -- Http Layer
        assertEquals(DummyHttpServiceModule.class, core.getHttpLayer().getClass());
    }

    @Test(expected = InitializationException.class)
    public void testInitWithWrongConfigFile() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
    }

    @Test()
    public void testInitWithDifferentConfigFile() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("dummy-mobile-services.json");

        MobileCore core = MobileCore.init(context, options);
        DummyHttpServiceModule service = (DummyHttpServiceModule)
            core.getInstance(DummyHttpServiceModule.class);

        assertEquals("http://dummy.net", service.getUrl());
    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testConfigurationNotFoundException() {
        Application context = RuntimeEnvironment.application;

        MobileCore core = MobileCore.init(context);
        DummyHttpServiceModule service = (DummyHttpServiceModule)
            core.getInstance(DummyHttpServiceModule.class);

        assertNotNull(service);
    }

    @Test
    public void testGetInstance() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("dummy-mobile-services.json");

        MobileCore core = MobileCore.init(context, options);
        DummyHttpServiceModule service = (DummyHttpServiceModule)
            core.getInstance(DummyHttpServiceModule.class);

        assertNotNull(service);
    }

    @Test
    public void testGetCachedInstance() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("dummy-mobile-services.json");

        MobileCore core = MobileCore.init(context, options);

        DummyHttpServiceModule service1 = (DummyHttpServiceModule)
            core.getInstance(DummyHttpServiceModule.class);

        DummyHttpServiceModule service2 = (DummyHttpServiceModule)
            core.getInstance(DummyHttpServiceModule.class);

        assertNotNull(service1);
        assertNotNull(service2);
        assertEquals(service1, service2);
    }

    // -- Helpers ---------------------------------------------------------------------------------

    public static final class DummyHttpServiceModule implements HttpServiceModule {

        private String uri;

        @Override
        public HttpRequest newRequest() {
            return null;
        }

        @Override
        public String type() {
            return "dummy";
        }

        @Override
        public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
            uri = serviceConfiguration.getUri();
        }

        @Override
        public void destroy() {
        }

        public String getUrl() {
            return this.uri;
        }

    }

}
