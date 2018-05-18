package org.aerogear.mobile.auth.configuration;

import net.openid.appauth.browser.Browsers;
import net.openid.appauth.browser.VersionRange;
import net.openid.appauth.browser.VersionedBrowserMatcher;

public class Browser {

    private VersionedBrowserMatcher versionedBrowserMatcher;

    public Browser(final BrowserBuilder builder) {
        this.versionedBrowserMatcher = configureBrowser(builder);
    }

    public static class BrowserBuilder {
        private BrowserType browser;
        private Boolean customTab;
        private VersionRange versionRange;

        public BrowserBuilder() {}

        public BrowserBuilder browser(BrowserType browser) {
            this.browser = browser;
            return this;
        }

        public BrowserBuilder customTab(Boolean customTab) {
            this.customTab = customTab;
            return this;
        }

        public BrowserBuilder versionRangeAtMost(String versionRange) {
            this.versionRange = VersionRange.atMost(versionRange);
            return this;
        }

        public BrowserBuilder versionRangeAtLeast(String versionRange) {
            this.versionRange = VersionRange.atLeast(versionRange);
            return this;
        }

        public BrowserBuilder versionRangeBetween(String upperBound, String lowerBound) {
            this.versionRange = VersionRange.between(upperBound, lowerBound);
            return this;
        }

        public BrowserBuilder versionRangeAnyVersion() {
            this.versionRange = VersionRange.ANY_VERSION;
            return this;
        }

        public Browser build() {
            return new Browser(this);
        }
    }

    private VersionedBrowserMatcher configureBrowser(BrowserBuilder builder) {
        VersionedBrowserMatcher vbm = null;
        switch (builder.browser) {
            case CHROME:
                vbm = configureChromeBrowser(builder);
                break;
            case CHROME_DEFAULT:
                vbm = VersionedBrowserMatcher.CHROME_BROWSER;
                break;
            case CHROME_DEFAULT_CUSTOM_TAB:
                vbm = VersionedBrowserMatcher.CHROME_CUSTOM_TAB;
                break;
            case FIREFOX:
                vbm = configureFirefoxBrowser(builder);
                break;
            case FIREFOX_DEFAULT:
                vbm = VersionedBrowserMatcher.FIREFOX_BROWSER;
                break;
            case SAMSUNG_BROWSER:
                vbm = configureSBrowser(builder);
                break;
            case SAMSUNG_BROWSER_DEFAULT:
                vbm = VersionedBrowserMatcher.SAMSUNG_BROWSER;
                break;
            case SAMSUNG_BROWSER_DEFAULT_CUSTOM_TAB:
                vbm = VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB;
                break;
        }
        return vbm;
    }

    private VersionedBrowserMatcher configureChromeBrowser(BrowserBuilder builder) {
        return new VersionedBrowserMatcher(Browsers.Chrome.PACKAGE_NAME,
                        Browsers.Chrome.SIGNATURE_SET, builder.customTab, builder.versionRange);
    }

    private VersionedBrowserMatcher configureFirefoxBrowser(BrowserBuilder builder) {
        return new VersionedBrowserMatcher(Browsers.Firefox.PACKAGE_NAME,
                        Browsers.Firefox.SIGNATURE_SET, builder.customTab, builder.versionRange);
    }

    private VersionedBrowserMatcher configureSBrowser(BrowserBuilder builder) {
        return new VersionedBrowserMatcher(Browsers.SBrowser.PACKAGE_NAME,
                        Browsers.SBrowser.SIGNATURE_SET, builder.customTab, builder.versionRange);
    }

    public VersionedBrowserMatcher getVersionedBrowserMatcher() {
        return versionedBrowserMatcher;
    }
}
