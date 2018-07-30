package org.aerogear.mobile.core.unit.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.core.categories.UnitTest;
import org.aerogear.mobile.core.configuration.https.CertificatePinningEntry;


@RunWith(RobolectricTestRunner.class)
@Category(UnitTest.class)
public class CertificatePinningEntryTest {

    private final static String TEST_HOST = "aerogear.org";
    private final static String TEST_HASH = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private final static String EXPECTED_HASH = "sha256/" + TEST_HASH;

    @Test
    public void testCreateConfig() {
        CertificatePinningEntry configuration = new CertificatePinningEntry(TEST_HOST, TEST_HASH);

        Assert.assertEquals(TEST_HOST, configuration.getHostName());
        Assert.assertEquals(EXPECTED_HASH, configuration.getCertificateHash());
    }
}
