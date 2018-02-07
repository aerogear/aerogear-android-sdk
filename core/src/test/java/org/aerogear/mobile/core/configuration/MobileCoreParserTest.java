package org.aerogear.mobile.core.configuration;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreParserTest {

    @Test
    public void testMobileCoreParsing() throws IOException {
        Application context = RuntimeEnvironment.application;

        try (InputStream configStream = context.getAssets().open("mobile-services.json")) {
            Map<String, ServiceConfiguration> configs = MobileCoreJsonParser.parse(configStream);

            assertNotNull(configs.get("prometheus"));

            ServiceConfiguration keyCloakServiceConfiguration = configs.get("keycloak");
            assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", keyCloakServiceConfiguration.getProperty("auth-server-url"));
        } catch (JSONException | IOException exception) {
            fail("mobile-services.json not file");
        }
    }

    @Test(expected = IOException.class)
    public void testConfigFileNotFound() throws IOException, JSONException {
        Application context = RuntimeEnvironment.application;
        InputStream configStream = context.getAssets().open("wrong-file-name.json");

        MobileCoreJsonParser.parse(configStream);
    }

}

