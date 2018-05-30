package org.aerogear.mobile.auth.configuration;

/**
 * Represents the different browser options available.
 */
public enum BrowserType {
    /**
     * This represents a Google Chrome browser where the custom tab and versions can be specified.
     */
    CHROME,
    /**
     * This represents any version of a Google Chrome browser for use as a standalone browser.
     */
    CHROME_DEFAULT,
    /**
     * This represents the minimum version (45) of a Google Chrome browser for use as a custom tab.
     */
    CHROME_DEFAULT_CUSTOM_TAB,
    /**
     * This represents a Mozilla Firefox browser where custom tab and versions can be specified.
     */
    FIREFOX,
    /**
     * This represents any version of a Mozilla Firefox browser for use as a standalone browser.
     */
    FIREFOX_DEFAULT,
    /**
     * This represents a Samsung browser where the custom tab and versions can be specified.
     */
    SAMSUNG_BROWSER,
    /**
     * This represents any version of a Samsung browser for use as a standalone browser.
     */
    SAMSUNG_BROWSER_DEFAULT,
    /**
     * This represents any version of a Samsung browser for use as a custom tab.
     */
    SAMSUNG_BROWSER_DEFAULT_CUSTOM_TAB
}
