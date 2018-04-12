package org.aerogear.mobile.core.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.app.Application;
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
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Request;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MobileCoreTest {

    private static final String DUMMY_MOBILE_SERVICES_JSON = "dummy-mobile-services.json";
    private static final String EMPTY_MOBILE_SERVICES_JSON = "empty-mobile-services.json";

    private MobileCore core;
    private MobileCore dummyCore;

    @Before
    public void setUp() throws Exception {
        Application context = RuntimeEnvironment.application;

        core = MobileCore.init(context);

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(DUMMY_MOBILE_SERVICES_JSON);
        dummyCore = MobileCore.init(context, options);
    }

    @Test
    public void testInit() {
        // -- Http
        assertEquals(OkHttpServiceModule.class, core.getHttpLayer().getClass());

        // -- Logger
        assertEquals(LoggerAdapter.class, core.getLogger().getClass());

        // -- Metrics
        assertEquals(MetricsService.class, core.getInstance(MetricsService.class).getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullContext() throws Exception {
        MobileCore.init(null);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullOptions() throws Exception {
        MobileCore.init(null, null);
    }

    @Test
    public void testDefaultConfigFile() throws Exception {
        assertEquals(MobileCore.DEFAULT_CONFIG_FILE_NAME, core.getConfigFileName());
    }

    @Test
    public void testInitWithDifferentHttpServiceModule() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setHttpServiceModule(new DummyHttpServiceModule());

        MobileCore core = MobileCore.init(context, options);

        assertEquals(DummyHttpServiceModule.class, core.getHttpLayer().getClass());
    }

    @Test
    public void testInitWithDifferentConfigFile() {
        DummyHttpServiceModule service = dummyCore.getInstance(DummyHttpServiceModule.class);

        assertEquals("http://dummy.net", service.getUrl());
    }

    @Test
    public void testInitWithDifferentLogger() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setLogger(new DummyLogger());

        MobileCore core = MobileCore.init(context, options);

        assertEquals(DummyLogger.class, core.getLogger().getClass());
    }

    @Test(expected = InitializationException.class)
    public void testInitWithNonExistentConfigFile() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
    }

    @Test
    public void testConfigurationNotRequiredDoesNotThrowException() {
        core.getInstance(DummyServiceModule.class);
    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testUnregisteredServiceThrowsException() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(EMPTY_MOBILE_SERVICES_JSON);
        MobileCore emptyCore = MobileCore.init(context, options);

        emptyCore.getInstance(DummyHttpServiceModule.class);
    }

    @Test
    public void testInitDoesNotThrowIfMetricsNotRegistered() throws Exception {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(EMPTY_MOBILE_SERVICES_JSON);
        MobileCore.init(context, options);
    }

    @Test
    public void testGetInstance() {
        DummyHttpServiceModule service = dummyCore.getInstance(DummyHttpServiceModule.class);

        assertNotNull(service);
    }

    @Test
    public void testGetCachedInstance() {
        DummyHttpServiceModule service1 = dummyCore.getInstance(DummyHttpServiceModule.class);
        DummyHttpServiceModule service2 = dummyCore.getInstance(DummyHttpServiceModule.class);

        assertNotNull(service1);
        assertNotNull(service2);
        assertEquals(service1, service2);
    }

    @Test
    public void testAllServicesAreDestroyed() throws Exception {
        DummyServiceModule service1 = dummyCore.getInstance(DummyServiceModule.class);
        DummyServiceModule service2 = dummyCore.getInstance(DummyServiceModule.class);

        Assert.assertFalse(service1.isDestroyed());
        Assert.assertFalse(service2.isDestroyed());

        dummyCore.destroy();

        Assert.assertTrue(service1.isDestroyed());
        Assert.assertTrue(service2.isDestroyed());
    }

    @Test
    public void testGetServiceConfiguration() {
        ServiceConfiguration serviceConfiguration = core.getServiceConfiguration("keycloak");

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
