package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.aerogear.android.core.R;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MobileCoreParserTest {

    @Test
    public void testAssetMobileCoreParsing() throws IOException {
        Application application = RuntimeEnvironment.application;
        MobileCore.Builder builder = new MobileCore.Builder(application);
        MobileCore core = builder.build();
        Assert.assertNotNull(core.getConfig("prometheus"));
        ServiceConfiguration config = core.getConfig("keycloak");
        org.junit.Assert.assertEquals("http://keycloak-myproject.192.168.37.1.nip.io/auth", config.getProperty("auth-server-url"));
        Assert.assertNull(core.getConfig("null"));
    }



}

