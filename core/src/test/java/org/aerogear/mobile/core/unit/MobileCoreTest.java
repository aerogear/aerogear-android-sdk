package org.aerogear.mobile.core.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.content.Context;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;
import org.aerogear.mobile.core.reactive.Request;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MobileCoreTest {

    private static final String DUMMY_MOBILE_SERVICES_JSON = "dummy-mobile-services.json";
    private static final String EMPTY_MOBILE_SERVICES_JSON = "empty-mobile-services.json";

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        MobileCore.init(context);
    }

    @Test
    public void testDefaultOptions() {
        assertEquals(MobileCore.DEFAULT_CONFIG_FILE_NAME,
                        MobileCore.getInstance().getConfigFileName());
        assertEquals(LoggerAdapter.class, MobileCore.getLogger().getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullContext() {
        MobileCore.init(null);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullOptions() {
        MobileCore.init(null, null);
    }

    @Test
    public void testInitWithDifferentConfigFile() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(DUMMY_MOBILE_SERVICES_JSON);

        MobileCore.init(context, options);

        // assertEquals("http://dummy.net", service.getUrl());
    }

    @Test
    public void testInitWithDifferentLogger() {
        MobileCore.Options options = new MobileCore.Options();
        options.setLogger(new DummyLogger());

        MobileCore.init(context, options);

        assertEquals(DummyLogger.class, MobileCore.getLogger().getClass());
    }

    @Test(expected = InitializationException.class)
    public void testInitWithNonExistentConfigFile() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
    }

    @Test
    public void testConfigurationNotRequiredDoesNotThrowException() {
        MobileCore.getInstance().getInstance(DummyServiceModule.class);
    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testUnregisteredServiceThrowsException() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(EMPTY_MOBILE_SERVICES_JSON);

        MobileCore.init(context, options);

        MobileCore.getInstance().getInstance(DummyHttpServiceModule.class);
    }

    @Test
    public void testInitDoesNotThrowIfMetricsNotRegistered() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(EMPTY_MOBILE_SERVICES_JSON);

        MobileCore.init(context, options);
    }

    @Test
    public void testGetInstance() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(DUMMY_MOBILE_SERVICES_JSON);
        MobileCore.init(context, options);

        DummyHttpServiceModule service =
                        MobileCore.getInstance().getInstance(DummyHttpServiceModule.class);

        assertNotNull(service);
    }

    @Test
    public void testGetCachedInstance() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(DUMMY_MOBILE_SERVICES_JSON);
        MobileCore.init(context, options);

        DummyHttpServiceModule service1 =
                        MobileCore.getInstance().getInstance(DummyHttpServiceModule.class);
        DummyHttpServiceModule service2 =
                        MobileCore.getInstance().getInstance(DummyHttpServiceModule.class);

        assertNotNull(service1);
        assertNotNull(service2);
        assertEquals(service1, service2);
    }

    @Test
    public void testAllServicesAreDestroyed() {
        MobileCore.init(context);

        DummyServiceModule service1 =
                        MobileCore.getInstance().getInstance(DummyServiceModule.class);
        DummyServiceModule service2 =
                        MobileCore.getInstance().getInstance(DummyServiceModule.class);

        Assert.assertFalse(service1.isDestroyed());
        Assert.assertFalse(service2.isDestroyed());

        MobileCore.destroy();

        Assert.assertTrue(service1.isDestroyed());
        Assert.assertTrue(service2.isDestroyed());
    }

    @Test
    public void testGetServiceConfiguration() {
        MobileCore.init(context);

        ServiceConfiguration serviceConfiguration =
                        MobileCore.getInstance().getServiceConfiguration("keycloak");

        String url = "https://www.mocky.io/v2/5a6b59fb31000088191b8ac6";
        assertEquals(url, serviceConfiguration.getUrl());
    }

    // -- Helpers ---------------------------------------------------------------------------------

    public static final class DummyServiceModule implements ServiceModule {

        private boolean destroyed = false;

        @Override
        public String type() {
            return "null";
        }

        @Override
        public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {}

        @Override
        public boolean requiresConfiguration() {
            return false;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }

    public static final class DummyHttpServiceModule implements HttpServiceModule {

        private String uri;

        @Override
        public HttpRequest newRequest() {
            return new HttpRequest() {
                @Override
                public HttpRequest addHeader(String key, String value) {
                    return this;
                }

                @Override
                public Request<HttpResponse> get(String url) {
                    return null;
                }

                @Override
                public Request<HttpResponse> post(String url, byte[] body) {
                    return null;
                }

                @Override
                public Request<HttpResponse> put(String url, byte[] body) {
                    return null;
                }

                @Override
                public Request<HttpResponse> delete(String url) {
                    return null;
                }

            };
        }

        @Override
        public String type() {
            return "dummy";
        }

        @Override
        public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
            uri = serviceConfiguration.getUrl();
        }

        @Override
        public boolean requiresConfiguration() {
            return true;
        }

        @Override
        public void destroy() {}

        public String getUrl() {
            return this.uri;
        }

    }

    public static final class DummyLogger implements Logger {

        @Override
        public void info(String tag, String message) {}

        @Override
        public void info(String message) {}

        @Override
        public void info(String tag, String message, Throwable e) {}

        @Override
        public void info(String message, Throwable e) {}

        @Override
        public void warning(String tag, String message) {}

        @Override
        public void warning(String message) {}

        @Override
        public void warning(String tag, String message, Throwable e) {}

        @Override
        public void warning(String message, Throwable e) {}

        @Override
        public void debug(String tag, String message) {}

        @Override
        public void debug(String message) {}

        @Override
        public void debug(String tag, String message, Throwable e) {}

        @Override
        public void debug(String message, Throwable e) {}

        @Override
        public void error(String tag, String message) {}

        @Override
        public void error(String message) {}

        @Override
        public void error(String tag, String message, Throwable e) {}

        @Override
        public void error(String message, Throwable e) {}

    }

}
