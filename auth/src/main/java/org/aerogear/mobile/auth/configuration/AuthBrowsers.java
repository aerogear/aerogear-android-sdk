package org.aerogear.mobile.auth.configuration;

import net.openid.appauth.browser.Browsers;

public class AuthBrowsers {

    public static final AuthBrowser CHROME_DEFAULT = new AuthBrowser(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET, false, AuthBrowserVersionRange.ANY);

    public static final AuthBrowser CHROME_CUSTOM_TAB = new AuthBrowser(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET, true, AuthBrowserVersionRange.ANY);

    public static final AuthBrowser FIREFOX_DEFAULT = new AuthBrowser(Browsers.Firefox.PACKAGE_NAME, Browsers.Firefox.SIGNATURE_SET, false, AuthBrowserVersionRange.ANY);

    //TODO: Add more pre-defined browsers
}
