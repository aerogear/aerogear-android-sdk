package org.aerogear.mobile.auth.configuration;

import org.junit.Assert;
import org.junit.Test;

public class AuthBrowsersTest {

    @Test
    public void testChromeDefault() {
        Assert.assertEquals("com.android.chrome", AuthBrowsers.CHROME_DEFAULT.getPackageName());
        Assert.assertTrue(AuthBrowsers.CHROME_DEFAULT.getSignatures().contains(
                        "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg=="));
        Assert.assertEquals(false, AuthBrowsers.CHROME_DEFAULT.isUseCustomTab());
        Assert.assertEquals(null, AuthBrowsers.CHROME_DEFAULT.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null, AuthBrowsers.CHROME_DEFAULT.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testChromeCustomTab() {
        Assert.assertEquals("com.android.chrome", AuthBrowsers.CHROME_CUSTOM_TAB.getPackageName());
        Assert.assertTrue(AuthBrowsers.CHROME_CUSTOM_TAB.getSignatures().contains(
                        "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg=="));
        Assert.assertEquals(true, AuthBrowsers.CHROME_CUSTOM_TAB.isUseCustomTab());
        Assert.assertEquals("45",
                        AuthBrowsers.CHROME_CUSTOM_TAB.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null,
                        AuthBrowsers.CHROME_CUSTOM_TAB.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testFirefoxDefault() {
        Assert.assertEquals("org.mozilla.firefox", AuthBrowsers.FIREFOX_DEFAULT.getPackageName());
        Assert.assertTrue(AuthBrowsers.FIREFOX_DEFAULT.getSignatures().contains(
                        "2gCe6pR_AO_Q2Vu8Iep-4AsiKNnUHQxu0FaDHO_qa178GByKybdT_BuE8_dYk99G5Uvx_gdONXAOO2EaXidpVQ=="));
        Assert.assertEquals(false, AuthBrowsers.FIREFOX_DEFAULT.isUseCustomTab());
        Assert.assertEquals(null,
                        AuthBrowsers.FIREFOX_DEFAULT.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null,
                        AuthBrowsers.FIREFOX_DEFAULT.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testSamsungDefault() {
        Assert.assertEquals("com.sec.android.app.sbrowser",
                        AuthBrowsers.SAMSUNG_DEFAULT.getPackageName());
        Assert.assertTrue(AuthBrowsers.SAMSUNG_DEFAULT.getSignatures().contains(
                        "ABi2fbt8vkzj7SJ8aD5jc4xJFTDFntdkMrYXL3itsvqY1QIw-dZozdop5rgKNxjbrQAd5nntAGpgh9w84O1Xgg=="));
        Assert.assertEquals(false, AuthBrowsers.SAMSUNG_DEFAULT.isUseCustomTab());
        Assert.assertEquals(null,
                        AuthBrowsers.SAMSUNG_DEFAULT.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null,
                        AuthBrowsers.SAMSUNG_DEFAULT.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testSamsungCustomTab() {
        Assert.assertEquals("com.sec.android.app.sbrowser",
                        AuthBrowsers.SAMSUNG_CUSTOM_TAB.getPackageName());
        Assert.assertTrue(AuthBrowsers.SAMSUNG_CUSTOM_TAB.getSignatures().contains(
                        "ABi2fbt8vkzj7SJ8aD5jc4xJFTDFntdkMrYXL3itsvqY1QIw-dZozdop5rgKNxjbrQAd5nntAGpgh9w84O1Xgg=="));
        Assert.assertEquals(true, AuthBrowsers.SAMSUNG_CUSTOM_TAB.isUseCustomTab());
        Assert.assertEquals(null,
                        AuthBrowsers.SAMSUNG_CUSTOM_TAB.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null,
                        AuthBrowsers.SAMSUNG_CUSTOM_TAB.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testCustomChrome() {
        AuthBrowser chromeCustom =
                        AuthBrowsers.chrome(false, AuthBrowserVersionRange.between("45", "50"));
        Assert.assertEquals("com.android.chrome", chromeCustom.getPackageName());
        Assert.assertTrue(chromeCustom.getSignatures().contains(
                        "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg=="));
        Assert.assertEquals(false, chromeCustom.isUseCustomTab());
        Assert.assertEquals("45", chromeCustom.getVersionRange().getLowerBoundary());
        Assert.assertEquals("50", chromeCustom.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testCustomFirefox() {
        AuthBrowser customFirefox =
                        AuthBrowsers.firefox(false, AuthBrowserVersionRange.atLeast("55"));
        Assert.assertEquals("org.mozilla.firefox", customFirefox.getPackageName());
        Assert.assertTrue(customFirefox.getSignatures().contains(
                        "2gCe6pR_AO_Q2Vu8Iep-4AsiKNnUHQxu0FaDHO_qa178GByKybdT_BuE8_dYk99G5Uvx_gdONXAOO2EaXidpVQ=="));
        Assert.assertEquals(false, customFirefox.isUseCustomTab());
        Assert.assertEquals("55", customFirefox.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null, customFirefox.getVersionRange().getUpperBoundary());
    }

    @Test
    public void testCustomSamsung() {
        AuthBrowser customSamsung =
                        AuthBrowsers.samsung(true, AuthBrowserVersionRange.atMost("6.0"));
        Assert.assertEquals("com.sec.android.app.sbrowser", customSamsung.getPackageName());
        Assert.assertTrue(customSamsung.getSignatures().contains(
                        "ABi2fbt8vkzj7SJ8aD5jc4xJFTDFntdkMrYXL3itsvqY1QIw-dZozdop5rgKNxjbrQAd5nntAGpgh9w84O1Xgg=="));
        Assert.assertEquals(true, customSamsung.isUseCustomTab());
        Assert.assertEquals(null, customSamsung.getVersionRange().getLowerBoundary());
        Assert.assertEquals("6.0", customSamsung.getVersionRange().getUpperBoundary());
    }
}
