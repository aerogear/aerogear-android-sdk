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
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.MetricsService;

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class MobileCoreTest {

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        MobileCore.init(context);
    }

    @Test(expected = RuntimeException.class)
    public void testInitWithNullContext() {
        MobileCore.init(null);
    }

    @Test
    public void testInitWithDifferentLogger() {
        MobileCore.setLogger(new DummyLogger());

        assertEquals(DummyLogger.class, MobileCore.getLogger().getClass());
    }

    @Test
    public void testConfigurationNotRequiredDoesNotThrowException() {
        MobileCore.getInstance().getService(DummyServiceModule.class);
    }

    @Test
    public void testGetInstance() {
        MobileCore.init(context);

        MetricsService service = MobileCore.getInstance().getService(MetricsService.class);

        assertNotNull(service);
    }

    @Test
    public void testGetCachedInstance() {
        MobileCore.init(context);

        MetricsService service1 = MobileCore.getInstance().getService(MetricsService.class);
        MetricsService service2 = MobileCore.getInstance().getService(MetricsService.class);

        assertNotNull(service1);
        assertNotNull(service2);
        assertEquals(service1, service2);
    }

    @Test
    public void testAllServicesAreDestroyed() {
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
