package org.aerogear.mobile.core.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class HttpsConfigurationTest {

    @Test
    public void testCreateConfig() {
        HttpsConfiguration configuration = HttpsConfiguration.newHashConfiguration()
                        .setHostName("aerogear.org")
                        .setCertificateHash("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();

        Assert.assertEquals("aerogear.org", configuration.getHostName());
        Assert.assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", configuration.getCertificateHash());
    }
}
