package org.aerogear.mobile.core.unit.configuration;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.categories.UnitTest;
import org.aerogear.mobile.core.configuration.MobileCoreConfiguration;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

@RunWith(AeroGearTestRunner.class)
@SmallTest
@Category(UnitTest.class)
public class MobileCoreParserTest {

    @Test
    public void testMobileCoreParsing() throws IOException {
        Application context = RuntimeEnvironment.application;

        try (InputStream configStream = context.getAssets().open("mobile-services.json")) {
            MobileCoreConfiguration jsonConfig = new MobileCoreJsonParser(configStream).parse();

            // test things by id
            {
                Map<String, ServiceConfiguration> configsPerId =
                                jsonConfig.getServicesConfigPerId();

                assertNotNull(configsPerId.get("metrics-myapp-android"));

                ServiceConfiguration keyCloakServiceConfiguration =
                                configsPerId.get("keycloak-myapp-android");
                assertEquals("https://keycloak-myproject.192.168.64.74.nip.io/auth",
                                keyCloakServiceConfiguration.getProperty("auth-server-url"));
            }

            // test things by type
            {
                Map<String, List<ServiceConfiguration>> configsPerType =
                                jsonConfig.getServiceConfigsPerType();

                assertTrue(configsPerType.get("metrics") != null
                                && configsPerType.get("metrics").size() == 1);

                List<ServiceConfiguration> keycloakConfigs = configsPerType.get("keycloak");
                assertTrue(keycloakConfigs != null && keycloakConfigs.size() == 1);
                ServiceConfiguration keycloakConfig = keycloakConfigs.get(0);
                assertEquals("https://keycloak-myproject.192.168.64.74.nip.io/auth",
                                keycloakConfig.getProperty("auth-server-url"));
            }
        } catch (JSONException | IOException exception) {
            System.out.println(exception);
            fail(exception.getMessage());
        }
    }

    @Test
    public void testMobileCoreParsingWithMultipleConfigsForServices() throws IOException {
        Application context = RuntimeEnvironment.application;

        try (InputStream configStream = context.getAssets()
                        .open("mobile-services-multiple-services-for-type.json")) {
            MobileCoreConfiguration jsonConfig = new MobileCoreJsonParser(configStream).parse();

            // test things by id
            {
                Map<String, ServiceConfiguration> configsPerId =
                                jsonConfig.getServicesConfigPerId();

                assertNotNull(configsPerId.get("metrics-myapp-android"));
                assertNotNull(configsPerId.get("metrics-yourapp-android"));

                ServiceConfiguration keyCloakServiceConfiguration =
                                configsPerId.get("keycloak-myapp-android");
                assertEquals("https://keycloak-myproject.192.168.64.74.nip.io/auth",
                                keyCloakServiceConfiguration.getProperty("auth-server-url"));
            }

            // test things by type
            {
                Map<String, List<ServiceConfiguration>> configsPerType =
                                jsonConfig.getServiceConfigsPerType();

                List<ServiceConfiguration> metricsConfigs = configsPerType.get("metrics");
                assertTrue(metricsConfigs != null && metricsConfigs.size() == 2);
                assertEquals("metrics-yourapp-android", metricsConfigs.get(0).getId());
                assertEquals("metrics-myapp-android", metricsConfigs.get(1).getId());


                List<ServiceConfiguration> keycloakConfigs = configsPerType.get("keycloak");
                assertTrue(keycloakConfigs != null && keycloakConfigs.size() == 1);
                ServiceConfiguration keycloakConfig = keycloakConfigs.get(0);
                assertEquals("https://keycloak-myproject.192.168.64.74.nip.io/auth",
                                keycloakConfig.getProperty("auth-server-url"));
            }

        } catch (JSONException | IOException exception) {
            System.out.println(exception);
            fail(exception.getMessage());
        }
    }

    @Test(expected = IOException.class)
    public void testConfigFileNotFound() throws IOException, JSONException {
        Application context = RuntimeEnvironment.application;
        InputStream configStream = context.getAssets().open("wrong-file-name.json");

        new MobileCoreJsonParser(configStream).parse();
    }

}
