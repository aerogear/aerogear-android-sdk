package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import net.openid.appauth.browser.Browsers;
import net.openid.appauth.browser.VersionRange;
import net.openid.appauth.browser.VersionedBrowserMatcher;

/**
 * Represents a browser by defining its type (Chrome, Firefox, Samsung DefinedBrowser), version and
 * whether it is being used as a custom tab during authentication. This can be used as part of a
 * {@link BrowserConfiguration} to whitelist or blacklist this {@link DefinedBrowser}.
 */
public class DefinedBrowser implements Browser {

    private VersionedBrowserMatcher versionedBrowserMatcher;

    private DefinedBrowser(final DefinedBrowserBuilder builder) {
        this.versionedBrowserMatcher = configureBrowser(builder);
    }

    /**
     * Builds and returns a {@link DefinedBrowser} object.
     */
    public static class DefinedBrowserBuilder {
        private DefinedBrowserType browser;
        private Boolean customTab;
        private VersionRange versionRange;

        public DefinedBrowserBuilder() {}

        /**
         * Specify the browser to be used during authentication.
         *
         * @param browser is the {@link DefinedBrowserType} to be used
         * @return the builder instance
         */
        public DefinedBrowserBuilder browser(DefinedBrowserType browser) {
            this.browser = browser;
            return this;
        }


        /**
         * Specify the to use the browser as a custom tab or not. This does not need to be set when
         * using any of the default {@link DefinedBrowserType}.
         *
         * @param customTab <code>true</code> to use the browser as a custom tab
         * @return the builder instance
         */
        public DefinedBrowserBuilder customTab(Boolean customTab) {
            this.customTab = customTab;
            return this;
        }

        /**
         * Specify the max version of the browser to be used. This does not need to be set when
         * using any of the default {@link DefinedBrowserType}.
         *
         * @param versionRange the max version of the browser to be used
         * @return the builder instance
         */
        public DefinedBrowserBuilder versionRangeAtMost(String versionRange) {
            this.versionRange = VersionRange.atMost(versionRange);
            return this;
        }

        /**
         * Specify the min version of the browser to be used. This does not need to be set when
         * using any of the default {@link DefinedBrowserType}.
         *
         * @param versionRange the min version of the browser to be used
         * @return the builder instance
         */
        public DefinedBrowserBuilder versionRangeAtLeast(String versionRange) {
            this.versionRange = VersionRange.atLeast(versionRange);
            return this;
        }

        /**
         * Specify the range of versions of the browser that can be used. This does not need to be
         * set when using any of the default {@link DefinedBrowserType}.
         *
         * @param upperBound the upper bound version of the version range
         * @param lowerBound the lower bound version of the version range
         * @return the builder instance
         */
        public DefinedBrowserBuilder versionRangeBetween(String upperBound, String lowerBound) {
            this.versionRange = VersionRange.between(upperBound, lowerBound);
            return this;
        }

        /**
         * Specifies any version of the browser available to be used. This does not need to be set
         * when using any of the default {@link DefinedBrowserType}.
         *
         * @return the builder instance
         */
        public DefinedBrowserBuilder versionRangeAnyVersion() {
            this.versionRange = VersionRange.ANY_VERSION;
            return this;
        }

        /**
         * Builds the browser object and performs some validation.
         *
         * @return {@link DefinedBrowser}
         */
        public DefinedBrowser build() {
            nonNull(browser, "browser must be set before building", "browser");
            nonNull(customTab, "must specifiy to use custom tab or not for custom browsers",
                            "customTab");
            nonNull(versionRange, "must specify version(s) to use for custom browsers",
                            "versionRange");
            return new DefinedBrowser(this);
        }
    }

    private VersionedBrowserMatcher configureBrowser(DefinedBrowserBuilder builder) {
        VersionedBrowserMatcher vbm = null;
        switch (builder.browser) {
            case CHROME:
                vbm = configureChromeBrowser(builder);
                break;
            case FIREFOX:
                vbm = configureFirefoxBrowser(builder);
                break;
            case SAMSUNG_BROWSER:
                vbm = configureSBrowser(builder);
                break;
        }
        return vbm;
    }

    private VersionedBrowserMatcher configureChromeBrowser(DefinedBrowserBuilder builder) {
        return new VersionedBrowserMatcher(Browsers.Chrome.PACKAGE_NAME,
                        Browsers.Chrome.SIGNATURE_SET, builder.customTab, builder.versionRange);
    }

    private VersionedBrowserMatcher configureFirefoxBrowser(DefinedBrowserBuilder builder) {
        return new VersionedBrowserMatcher(Browsers.Firefox.PACKAGE_NAME,
                        Browsers.Firefox.SIGNATURE_SET, builder.customTab, builder.versionRange);
    }

    private VersionedBrowserMatcher configureSBrowser(DefinedBrowserBuilder builder) {
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
