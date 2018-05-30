package org.aerogear.mobile.auth.configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.BrowserMatcher;
import net.openid.appauth.browser.BrowserWhitelist;

/**
 * Represents the browsers that are to be used or not used during authentication.
 */
public class BrowserConfiguration {

    private final boolean blackList;
    private final Set<Browser> browsers;
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
        private Set<Browser> browsers = new HashSet<>();

        public BrowserConfigurationBuilder() {}

        /**
         * Specifies the browsers should be blacklisted.
         *
         * @return the builder instance
         */
        public BrowserConfigurationBuilder blackList() {
            this.blackList = true;
            return this;
        }

        /**
         * Specifies the browsers should be whitelisted.
         *
         * @return the builder instance
         */
        public BrowserConfigurationBuilder whiteList() {
            this.blackList = false;
            return this;
        }

        /**
         * Specifies the browser to be either blacklisted or whitelisted.
         *
         * @param browser {@link Browser} object
         * @return the builder instance
         */
        public BrowserConfigurationBuilder browser(Browser browser) {
            this.browsers.add(browser);
            return this;
        }

        /**
         * Specifies a set of browsers to be either blacklisted or whitelisted.
         *
         * @param browsers a set of {@link Browser} objects
         * @return the builder instance
         */
        public BrowserConfigurationBuilder browsers(Set<Browser> browsers) {
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

    private AppAuthConfiguration blackOrWhiteListBrowsers(Set<Browser> browsers,
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

    private BrowserMatcher[] parseBrowserMatchers(Set<Browser> browsers) {
        Set<BrowserMatcher> parsedBrowserConfigs = new HashSet<>();
        Iterator iterator = browsers.iterator();
        while (iterator.hasNext()) {
            parsedBrowserConfigs.add(((Browser) iterator.next()).getVersionedBrowserMatcher());
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
