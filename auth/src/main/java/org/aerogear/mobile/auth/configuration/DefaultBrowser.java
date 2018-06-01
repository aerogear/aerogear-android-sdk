package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import net.openid.appauth.browser.VersionedBrowserMatcher;

public class DefaultBrowser implements Browser {
    private VersionedBrowserMatcher versionedBrowserMatcher;

    private DefaultBrowser(final DefaultBrowserBuilder builder) {
        this.versionedBrowserMatcher = configureBrowser(builder);
    }

    /**
     * Builds and returns a {@link DefinedBrowser} object.
     */
    public static class DefaultBrowserBuilder {
        private DefaultBrowserType browser;

        public DefaultBrowserBuilder() {}

        /**
         * Specify the browser to be used during authentication.
         *
         * @param browser is the {@link DefinedBrowserType} to be used
         * @return the builder instance
         */
        public DefaultBrowserBuilder browser(DefaultBrowserType browser) {
            this.browser = browser;
            return this;
        }

        /**
         * Builds the browser object and performs some validation.
         *
         * @return {@link DefinedBrowser}
         */
        public DefaultBrowser build() {
            nonNull(browser, "browser must be set before building", "browser");
            return new DefaultBrowser(this);
        }
    }

    private VersionedBrowserMatcher configureBrowser(DefaultBrowserBuilder builder) {
        VersionedBrowserMatcher vbm = null;
        switch (builder.browser) {
            case CHROME_DEFAULT:
                vbm = VersionedBrowserMatcher.CHROME_BROWSER;
                break;
            case CHROME_DEFAULT_CUSTOM_TAB:
                vbm = VersionedBrowserMatcher.CHROME_CUSTOM_TAB;
                break;
            case FIREFOX_DEFAULT:
                vbm = VersionedBrowserMatcher.FIREFOX_BROWSER;
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
