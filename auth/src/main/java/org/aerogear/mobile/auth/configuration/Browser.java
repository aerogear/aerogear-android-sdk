package org.aerogear.mobile.auth.configuration;

import net.openid.appauth.browser.VersionedBrowserMatcher;

public interface Browser {

    VersionedBrowserMatcher getVersionedBrowserMatcher();
}
