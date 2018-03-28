package org.aerogear.mobile.core.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.core.configuration.https.CertificatePinningEntry;


@RunWith(RobolectricTestRunner.class)
public class CertificatePinningEntryTest {

    private final static String TEST_HOST = "aerogear.org";
    private final static String TEST_HASH = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    @Test
    public void testCreateConfig() {
        CertificatePinningEntry configuration = new CertificatePinningEntry(TEST_HOST, TEST_HASH);

        Assert.assertEquals(TEST_HOST, configuration.getHostName());
        Assert.assertEquals(TEST_HASH, configuration.getCertificateHash());
    }
}
