package org.aerogear.mobile.auth.configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.BrowserMatcher;
import net.openid.appauth.browser.BrowserWhitelist;

public class BrowserConfiguration {

    private final boolean blackList;
    private final Set<Browser> browsers;
    private final AppAuthConfiguration appAuthConfig;

    private BrowserConfiguration(final BrowserConfigurationBuilder builder) {
        this.blackList = builder.blackList;
        this.browsers = new HashSet<>(builder.browsers);
        this.appAuthConfig = blackOrWhiteListBrowsers(builder.browsers, this.blackList);
    }


    public static class BrowserConfigurationBuilder {
        private boolean blackList;
        private Set<Browser> browsers = new HashSet<>();

        public BrowserConfigurationBuilder() {}

        public BrowserConfigurationBuilder blackList() {
            this.blackList = true;
            return this;
        }

        public BrowserConfigurationBuilder whiteList() {
            this.blackList = false;
            return this;
        }

        public BrowserConfigurationBuilder browser(Browser browser) {
            this.browsers.add(browser);
            return this;
        }

        public BrowserConfigurationBuilder browsers(Set<Browser> browsers) {
            this.browsers.addAll(browsers);
            return this;
        }

        public BrowserConfiguration build() {
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

    public AppAuthConfiguration getAppAuthConfig() {
        return appAuthConfig;
    }
}
