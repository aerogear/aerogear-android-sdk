package org.aerogear.mobile.core.unit;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.content.Context;
import android.support.test.filters.SmallTest;

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

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreTest {

    private static final String DUMMY_MOBILE_SERVICES_JSON = "dummy-mobile-services.json";
    private static final String EMPTY_MOBILE_SERVICES_JSON = "empty-mobile-services.json";

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
        MobileCore.init(context);
    }

    @Test
    public void testDefaultOptions() {
        assertEquals(MobileCore.DEFAULT_CONFIG_FILE_NAME,
                        MobileCore.getInstance().getConfigFileName());
        assertEquals(LoggerAdapter.class, MobileCore.getInstance().getLogger().getClass());
        assertEquals(OkHttpServiceModule.class, MobileCore.getInstance().getHttpLayer().getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullContext() throws Exception {
        MobileCore.init(null);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullOptions() throws Exception {
        MobileCore.init(context, null);
    }

    @Test
    public void testInitWithDifferentHttpServiceModule() {
        MobileCore.Options options = new MobileCore.Options();
        options.setHttpServiceModule(new DummyHttpServiceModule());

        MobileCore.init(context, options);

        assertEquals(DummyHttpServiceModule.class,
                        MobileCore.getInstance().getHttpLayer().getClass());
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

        assertEquals(DummyLogger.class, MobileCore.getInstance().getLogger().getClass());
    }

    @Test(expected = InitializationException.class)
    public void testInitWithNonExistentConfigFile() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
    }

    @Test
    public void testConfigurationNotRequiredDoesNotThrowException() {
        MobileCore.getInstance().getService(DummyServiceModule.class);
    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testUnregisteredServiceThrowsException() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(EMPTY_MOBILE_SERVICES_JSON);

        MobileCore.init(context, options);

        MobileCore.getInstance().getService(DummyHttpServiceModule.class);
    }

    @Test
    public void testInitDoesNotThrowIfMetricsNotRegistered() throws Exception {
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
                        MobileCore.getInstance().getService(DummyHttpServiceModule.class);

        assertNotNull(service);
    }

    @Test
    public void testGetCachedInstance() {
        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName(DUMMY_MOBILE_SERVICES_JSON);
        MobileCore.init(context, options);

        DummyHttpServiceModule service1 =
                        MobileCore.getInstance().getService(DummyHttpServiceModule.class);
        DummyHttpServiceModule service2 =
                        MobileCore.getInstance().getService(DummyHttpServiceModule.class);

        assertNotNull(service1);
        assertNotNull(service2);
        assertEquals(service1, service2);
    }

    @Test
    public void testAllServicesAreDestroyed() throws Exception {
        MobileCore.init(context);

        DummyServiceModule service1 = MobileCore.getInstance().getService(DummyServiceModule.class);
        DummyServiceModule service2 = MobileCore.getInstance().getService(DummyServiceModule.class);

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
                public void get(String url) {}

                @Override
                public void post(String url, byte[] body) {}

                @Override
                public void put(String url, byte[] body) {}

                @Override
                public void delete(String url) {}

                @Override
                public HttpResponse execute() {
                    return new HttpResponse() {
                        @Override
                        public HttpResponse onComplete(Runnable runnable) {
                            return this;
                        }

                        @Override
                        public HttpResponse onError(Runnable runnable) {
                            return this;
                        }

                        @Override
                        public HttpResponse onSuccess(Runnable runnable) {
                            return this;
                        }

                        @Override
                        public int getStatus() {
                            return HTTP_OK;
                        }

                        @Override
                        public void waitForCompletionAndClose() {}

                        @Override
                        public String stringBody() {
                            return "";
                        }

                        @Override
                        public Exception getError() {
                            return null;
                        }
                    };
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
