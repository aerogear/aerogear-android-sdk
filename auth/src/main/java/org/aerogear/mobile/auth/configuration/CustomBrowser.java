package org.aerogear.mobile.auth.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import net.openid.appauth.browser.VersionRange;
import net.openid.appauth.browser.VersionedBrowserMatcher;

public class CustomBrowser implements Browser {

    private VersionedBrowserMatcher versionedBrowserMatcher;

    private CustomBrowser(final CustomBrowserBuilder builder) {
        this.versionedBrowserMatcher = configureBrowser(builder);
    }

    public static class CustomBrowserBuilder {
        private String packageName;
        private String signature;
        private Boolean customTab;
        private VersionRange versionRange;

        public CustomBrowserBuilder() {}

        public CustomBrowserBuilder browser(String packageName, String signature) {
            this.packageName = packageName;
            this.signature = signature;
            return this;
        }

        /**
         * Specify the to use the browser as a custom tab or not. This does not need to be set when
         * using any of the default {@link DefinedBrowserType}.
         *
         * @param customTab <code>true</code> to use the browser as a custom tab
         * @return the builder instance
         */
        public CustomBrowserBuilder customTab(Boolean customTab) {
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
        public CustomBrowserBuilder versionRangeAtMost(String versionRange) {
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
        public CustomBrowserBuilder versionRangeAtLeast(String versionRange) {
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
        public CustomBrowserBuilder versionRangeBetween(String upperBound, String lowerBound) {
            this.versionRange = VersionRange.between(upperBound, lowerBound);
            return this;
        }

        /**
         * Specifies any version of the browser available to be used. This does not need to be set
         * when using any of the default {@link DefinedBrowserType}.
         *
         * @return the builder instance
         */
        public CustomBrowserBuilder versionRangeAnyVersion() {
            this.versionRange = VersionRange.ANY_VERSION;
            return this;
        }

        public CustomBrowser build() {
            nonNull(packageName, "must specify package name for custom browsers", "packageName");
            nonNull(signature, "must specifiy signature for custom browsers", "signature");
            nonNull(customTab, "must specifiy to use custom tab or not for custom browsers",
                            "customTab");
            nonNull(versionRange, "must specify version(s) to use for custom browsers",
                            "versionRange");
            return new CustomBrowser(this);
        }
    }

    private VersionedBrowserMatcher configureBrowser(CustomBrowserBuilder builder) {
        return new VersionedBrowserMatcher(builder.packageName, builder.signature,
                        builder.customTab, builder.versionRange);
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
