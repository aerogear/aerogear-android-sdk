package org.aerogear.mobile.auth.configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.BrowserMatcher;
import net.openid.appauth.browser.BrowserWhitelist;
import net.openid.appauth.browser.DelimitedVersion;
import net.openid.appauth.browser.VersionRange;
import net.openid.appauth.browser.VersionedBrowserMatcher;

/**
 * Represents the definedBrowsers that are to be used or not used during authentication.
 */
public class BrowserConfiguration {

    private final boolean blackList;
    private final Set<AuthBrowser> browsers;
    private final AppAuthConfiguration appAuthConfig;

    private BrowserConfiguration(final BrowserConfigurationBuilder builder) {
        this.blackList = builder.blackList;
        this.browsers = new HashSet<>(builder.browsers);
        this.appAuthConfig = blackOrWhiteListBrowsers(builder.browsers, this.blackList);
    }


    /**
     * Builds and returns a {@link BrowserConfiguration} object.
     */
    public static class BrowserConfigurationBuilder {
        private boolean blackList;
        private Set<AuthBrowser> browsers = new HashSet<>();

        public BrowserConfigurationBuilder() {}

        /**
         * Specifies the definedBrowsers should be blacklisted.
         *
         * @return the builder instance
         */
        public BrowserConfigurationBuilder blackList() {
            this.blackList = true;
            return this;
        }

        /**
         * Specifies the definedBrowsers should be whitelisted.
         *
         * @return the builder instance
         */
        public BrowserConfigurationBuilder whiteList() {
            this.blackList = false;
            return this;
        }

        /**
         * Specifies the definedBrowser to be either blacklisted or whitelisted.
         *
         * @param browser {@link AuthBrowser} object
         * @return the builder instance
         */
        public BrowserConfigurationBuilder browser(AuthBrowser browser) {
            this.browsers.add(browser);
            return this;
        }

        /**
         * Specifies a set of definedBrowsers to be either blacklisted or whitelisted.
         *
         * @param browsers a set of {@link AuthBrowser} objects
         * @return the builder instance
         */
        public BrowserConfigurationBuilder browsers(Set<AuthBrowser> browsers) {
            this.browsers.addAll(browsers);
            return this;
        }

        /**
         * Builds the browser configuration object and performs some validation.
         *
         * @return {@link BrowserConfiguration}
         */
        public BrowserConfiguration build() {
            if (browsers.isEmpty()) {
                throw new IllegalStateException(
                                "at least one browser must be specified to blacklist or whitelist");
            }
            return new BrowserConfiguration(this);
        }
    }

    private AppAuthConfiguration blackOrWhiteListBrowsers(Set<AuthBrowser> browsers,
                    Boolean blackList) {
        AppAuthConfiguration appAuthConfig;
        if (blackList) {
            appAuthConfig = new AppAuthConfiguration.Builder()
                            .setBrowserMatcher(new BrowserBlacklist(parseBrowserMatchers(browsers)))
                            .build();
        } else {
            appAuthConfig = new AppAuthConfiguration.Builder()
                            .setBrowserMatcher(new BrowserWhitelist(parseBrowserMatchers(browsers)))
                            .build();
        }

        return appAuthConfig;
    }

    private BrowserMatcher[] parseBrowserMatchers(Set<AuthBrowser> browsers) {
        Set<BrowserMatcher> parsedBrowserConfigs = new HashSet<>();
        Iterator iterator = browsers.iterator();
        while (iterator.hasNext()) {
            AuthBrowser browser = (AuthBrowser) iterator.next();
            VersionRange versionRange = new VersionRange(DelimitedVersion.parse(browser.getVersionRange().getLowerBoundry()), DelimitedVersion.parse(browser.getVersionRange().getUpperBoundry()));
            VersionedBrowserMatcher matcher = new VersionedBrowserMatcher(browser.getPackageName(), browser.getSignatures(), browser.isUseCustomTab(), versionRange);
            parsedBrowserConfigs.add(matcher);
        }
        return parsedBrowserConfigs.toArray(new BrowserMatcher[parsedBrowserConfigs.size()]);
    }

    /**
     * Gets the {@link AppAuthConfiguration} that is required by the auth service.
     *
     * @return {@link AppAuthConfiguration}
     */
    public AppAuthConfiguration getAppAuthConfig() {
        return appAuthConfig;
    }
}
