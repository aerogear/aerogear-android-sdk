package org.aerogear.mobile.auth.configuration;

import net.openid.appauth.browser.Browsers;

/**
 * Represents pre-defined authentication browsers (Google Chrome, Mozilla Firefox and Samsung
 * Browser). The supported browsers can be customised to be used as standalone or custom tab
 * browsers and can target a specific version or version range. There are also default options where
 * no extra configuration is needed.
 */
public class AuthBrowsers {

    /**
     * This targets any version of a Google Chrome browser for use as a standalone browser.
     */
    public static final AuthBrowser CHROME_DEFAULT = new AuthBrowser(Browsers.Chrome.PACKAGE_NAME,
                    Browsers.Chrome.SIGNATURE_SET, false, AuthBrowserVersionRange.ANY);

    /**
     * This targets at least version 45 of a Google Chrome browser for use as a custom tab.
     */
    public static final AuthBrowser CHROME_CUSTOM_TAB =
                    new AuthBrowser(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET,
                                    true, AuthBrowserVersionRange.atLeast("45"));

    /**
     * This targets any version of a Mozilla Firefox browser for use as a standalone browser.
     */
    public static final AuthBrowser FIREFOX_DEFAULT = new AuthBrowser(Browsers.Firefox.PACKAGE_NAME,
                    Browsers.Firefox.SIGNATURE_SET, false, AuthBrowserVersionRange.ANY);

    /**
     * This targets any version of a Samsung browser for use as a standalone browser.
     */
    public static final AuthBrowser SAMSUNG_DEFAULT =
                    new AuthBrowser(Browsers.SBrowser.PACKAGE_NAME, Browsers.SBrowser.SIGNATURE_SET,
                                    false, AuthBrowserVersionRange.ANY);

    /**
     * This targets any version of a Samsung browser for use as a custom tab.
     */
    public static final AuthBrowser SAMSUNG_CUSTOM_TAB =
                    new AuthBrowser(Browsers.SBrowser.PACKAGE_NAME, Browsers.SBrowser.SIGNATURE_SET,
                                    true, AuthBrowserVersionRange.ANY);

    /**
     * This targets the version range specified of a Google Chrome browser for use as the custom tab
     * mode specified.
     *
     * @param useCustomTab <code>true</code> to be used as a custom tab browser
     * @param versionRange the version range the browser should be
     * @return {@link AuthBrowser}
     */
    public static final AuthBrowser chrome(boolean useCustomTab,
                    AuthBrowserVersionRange versionRange) {
        return new AuthBrowser(Browsers.Chrome.PACKAGE_NAME, Browsers.Chrome.SIGNATURE_SET,
                        useCustomTab, versionRange);
    }

    /**
     * This targets the version range specified of a Mozilla Firefox browser for use as the custom
     * tab mode specified.
     *
     * @param useCustomTab <code>true</code> to be used as a custom tab browser
     * @param versionRange the version range the browser should be
     * @return {@link AuthBrowser}
     */
    public static final AuthBrowser firefox(boolean useCustomTab,
                    AuthBrowserVersionRange versionRange) {
        return new AuthBrowser(Browsers.Firefox.PACKAGE_NAME, Browsers.Firefox.SIGNATURE_SET,
                        useCustomTab, versionRange);
    }

    /**
     * This targets the version range specified of a Samsung browser for use as the custom tab mode
     * specified.
     *
     * @param useCustomTab <code>true</code> to be used as a custom tab browser
     * @param versionRange the version range the browser should be
     * @return {@link AuthBrowser}
     */
    public static AuthBrowser samsung(boolean useCustomTab, AuthBrowserVersionRange versionRange) {
        return new AuthBrowser(Browsers.SBrowser.PACKAGE_NAME, Browsers.SBrowser.SIGNATURE_SET,
                        useCustomTab, versionRange);
    }
}
