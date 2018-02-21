package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static java.net.HttpURLConnection.HTTP_OK;
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

        // -- Logger
        assertEquals(LoggerAdapter.class, core.getLogger().getClass());

        // -- Metrics
        assertEquals(MetricsService.class, core.getInstance(MetricsService.class).getClass());
    }

    @Test
    public void testInitWithDifferentHttpServiceModule() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setHttpServiceModule(new DummyHttpServiceModule());

        MobileCore core = MobileCore.init(context, options);

        assertEquals(DummyHttpServiceModule.class, core.getHttpLayer().getClass());
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

    @Test
    public void testInitWithDifferentLogger() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setLogger(new DummyLogger());

        MobileCore core = MobileCore.init(context, options);

        assertEquals(DummyLogger.class, core.getLogger().getClass());
    }

    @Test(expected = InitializationException.class)
    public void testInitWithWrongConfigFile() {
        Application context = RuntimeEnvironment.application;

        MobileCore.Options options = new MobileCore.Options();
        options.setConfigFileName("wrong-file-name.json");

        MobileCore.init(context, options);
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
            return new HttpRequest() {
                @Override
                public HttpRequest addHeader(String key, String value) {
                    return this;
                }

                @Override
                public void get(String url) {
                }

                @Override
                public void post(String url, byte[] body) {
                }

                @Override
                public void put(String url, byte[] body) {
                }

                @Override
                public void delete(String url) {
                }

                @Override
                public HttpResponse execute() {
                    return new HttpResponse() {
                        @Override
                        public HttpResponse onComplete(Runnable runnable) {
                            return this;
                        }

                        @Override
                        public int getStatus() {
                            return HTTP_OK;
                        }

                        @Override
                        public void waitForCompletionAndClose() {
                        }

                        @Override
                        public String stringBody() {
                            return "";
                        }

                        @Override
                        public boolean requestFailed() {
                            return false;
                        }

                        @Override
                        public Exception getRequestError() {
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
        public boolean requiresConfiguration() { return true; }

        @Override
        public void destroy() {
        }

        public String getUrl() {
            return this.uri;
        }

    }

    public static final class DummyLogger implements Logger {

        @Override
        public void info(String tag, String message) {
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void info(String tag, String message, Exception e) {
        }

        @Override
        public void info(String message, Exception e) {
        }

        @Override
        public void warning(String tag, String message) {
        }

        @Override
        public void warning(String message) {
        }

        @Override
        public void warning(String tag, String message, Exception e) {
        }

        @Override
        public void warning(String message, Exception e) {
        }

        @Override
        public void debug(String tag, String message) {
        }

        @Override
        public void debug(String message) {
        }

        @Override
        public void debug(String tag, String message, Exception e) {
        }

        @Override
        public void debug(String message, Exception e) {
        }

        @Override
        public void error(String tag, String message) {
        }

        @Override
        public void error(String message) {
        }

        @Override
        public void error(String tag, String message, Exception e) {
        }

        @Override
        public void error(String message, Exception e) {
        }

    }

}
