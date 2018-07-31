package org.aerogear.mobile.core.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.content.Context;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.categories.UnitTest;
import org.aerogear.mobile.core.configuration.MobileCoreConfiguration;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.logging.Logger;

@RunWith(AeroGearTestRunner.class)
@SmallTest
@Category(UnitTest.class)
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
    public void testGetServiceConfiguration() {
        MobileCore.init(context);
        DummyMobileCore.init(context);

        ServiceConfiguration serviceConfiguration =
                        MobileCore.getInstance().getServiceConfigurationByType("keycloak");
        ServiceConfiguration scUpperCaseType =
                        MobileCore.getInstance().getServiceConfigurationByType("KEYCLOAK");
        ServiceConfiguration scUpperAndLowerCase =
                        MobileCore.getInstance().getServiceConfigurationByType("KeYcLoAk");
        ServiceConfiguration dummySC =
                        DummyMobileCore.getInstance().getServiceConfigurationByType("keycloak");

        String url = "https://www.mocky.io/v2/5a6b59fb31000088191b8ac6";
        assertEquals(url, serviceConfiguration.getUrl());
        assertEquals(url, scUpperCaseType.getUrl());
        assertEquals(url, scUpperAndLowerCase.getUrl());
        assertEquals(url, dummySC.getUrl());
    }

    // -- Helpers ---------------------------------------------------------------------------------

    public static final class DummyMobileCore {
        private final String configFileName = "mobile-services-type-casing.json";
        private final Map<String, List<ServiceConfiguration>> serviceConfigsByType;
        private static DummyMobileCore instance;

        public static void init(final Context context) throws InitializationException {
            instance = new DummyMobileCore(context);
        }

        private DummyMobileCore(final Context context) {
            try (final InputStream configStream = context.getAssets().open(configFileName)) {
                MobileCoreConfiguration jsonConfig = new MobileCoreJsonParser(configStream).parse();
                serviceConfigsByType = jsonConfig.getServiceConfigsPerType();
            } catch (JSONException | IOException exception) {
                String message = String.format("%s could not be loaded", configFileName);
                throw new InitializationException(message, exception);
            }
        }

        public static DummyMobileCore getInstance() {
            return instance;
        }

        public ServiceConfiguration getServiceConfigurationByType(final String type) {
            final List<ServiceConfiguration> configs = serviceConfigsByType.get(type.toLowerCase());
            if (configs == null || configs.isEmpty()) {
                return null;
            }
            return configs.get(0);
        }

    }

    public static final class DummyServiceModule implements ServiceModule {

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
