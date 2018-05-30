package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import net.openid.appauth.browser.Browsers;
import net.openid.appauth.browser.VersionRange;
import net.openid.appauth.browser.VersionedBrowserMatcher;

/**
 * Represents a browser by defining its type (Chrome, Firefox, Samsung Browser), version and whether
 * it is being used as a custom tab during authentication. This can be used as part of a
 * {@link BrowserConfiguration} to whitelist or blacklist this {@link Browser}.
 */
public class Browser {

    private VersionedBrowserMatcher versionedBrowserMatcher;

    private Browser(final BrowserBuilder builder) {
        this.versionedBrowserMatcher = configureBrowser(builder);
    }

    /**
     * Builds and returns a {@link Browser} object.
     */
    public static class BrowserBuilder {
        private BrowserType browser;
        private Boolean customTab;
        private VersionRange versionRange;

        public BrowserBuilder() {}

        /**
         * Specify the browser to be used during authentication.
         *
         * @param browser is the {@link BrowserType} to be used
         * @return the builder instance
         */
        public BrowserBuilder browser(BrowserType browser) {
            this.browser = browser;
            return this;
        }

        /**
         * Specify the to use the browser as a custom tab or not. This does not need to be set when
         * using any of the default {@link BrowserType}.
         *
         * @param customTab <code>true</code> to use the browser as a custom tab
         * @return the builder instance
         */
        public BrowserBuilder customTab(Boolean customTab) {
            this.customTab = customTab;
            return this;
        }

        /**
         * Specify the max version of the browser to be used. This does not need to be set when
         * using any of the default {@link BrowserType}.
         *
         * @param versionRange the max version of the browser to be used
         * @return the builder instance
         */
        public BrowserBuilder versionRangeAtMost(String versionRange) {
            this.versionRange = VersionRange.atMost(versionRange);
            return this;
        }

        /**
         * Specify the min version of the browser to be used. This does not need to be set when
         * using any of the default {@link BrowserType}.
         *
         * @param versionRange the min version of the browser to be used
         * @return the builder instance
         */
        public BrowserBuilder versionRangeAtLeast(String versionRange) {
            this.versionRange = VersionRange.atLeast(versionRange);
            return this;
        }

        /**
         * Specify the range of versions of the browser that can be used. This does not need to be
         * set when using any of the default {@link BrowserType}.
         *
         * @param upperBound the upper bound version of the version range
         * @param lowerBound the lower bound version of the version range
         * @return the builder instance
         */
        public BrowserBuilder versionRangeBetween(String upperBound, String lowerBound) {
            this.versionRange = VersionRange.between(upperBound, lowerBound);
            return this;
        }

        /**
         * Specifies any version of the browser available to be used. This does not need to be set
         * when using any of the default {@link BrowserType}.
         *
         * @return the builder instance
         */
        public BrowserBuilder versionRangeAnyVersion() {
            this.versionRange = VersionRange.ANY_VERSION;
            return this;
        }

        /**
         * Builds the browser object and performs some validation.
         *
         * @return {@link Browser}
         */
        public Browser build() {
            nonNull(browser, "a browser must be specified", "browser");
            if (browser.equals(BrowserType.CHROME) || browser.equals(BrowserType.FIREFOX)
                            || browser.equals(BrowserType.SAMSUNG_BROWSER)) {
                nonNull(customTab,
                                "to use a custom tab or not must be specified for custom browser configs",
                                "customTab");
                nonNull(versionRange, "version range for custom browser configs must be specified",
                                "versionRange");
            }
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

    /**
     * Gets the {@link VersionedBrowserMatcher} that is used in {@link BrowserConfiguration} to
     * instantiate {@link net.openid.appauth.AppAuthConfiguration}.
     *
     * @return {@link VersionedBrowserMatcher}
     */
    public VersionedBrowserMatcher getVersionedBrowserMatcher() {
        return versionedBrowserMatcher;
    }
}
