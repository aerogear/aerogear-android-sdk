package org.aerogear.mobile.auth.configuration;

import org.junit.Assert;
import org.junit.Test;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.BrowserWhitelist;


public class BrowserConfigurationTest {

    @Test
    public void testChromeCustomTabBlackListSuccess() {
        AuthBrowser chromeCustomTab = AuthBrowsers.CHROME_CUSTOM_TAB;
        BrowserConfiguration browserConfiguration =
                        new BrowserConfiguration.BrowserConfigurationBuilder().blackList()
                                        .browser(chromeCustomTab).build();


        AppAuthConfiguration appAuthConfiguration = browserConfiguration.getAppAuthConfig();
        BrowserBlacklist browserMatcher = (BrowserBlacklist) browserConfiguration.getAppAuthConfig()
                        .getBrowserMatcher();

        Assert.assertNotNull(appAuthConfiguration);
        Assert.assertNotNull(browserMatcher);
    }

    @Test(expected = ClassCastException.class)
    public void testChromeCustomTabBlackListFail() {
        AuthBrowser chromeCustomTab = AuthBrowsers.CHROME_CUSTOM_TAB;
        BrowserConfiguration browserConfiguration =
                        new BrowserConfiguration.BrowserConfigurationBuilder().blackList()
                                        .browser(chromeCustomTab).build();


        BrowserWhitelist browserMatcher = (BrowserWhitelist) browserConfiguration.getAppAuthConfig()
                        .getBrowserMatcher();
    }

}
