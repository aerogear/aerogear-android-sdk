package org.aerogear.mobile.auth.configuration;

import java.util.Set;

/**
 * Represents a browser to be used or not used during authentication. This implementation is a light
 * weight wrapper around {@link net.openid.appauth.browser.VersionedBrowserMatcher}.
 */
public class AuthBrowser {

    private final String packageName;

    private final Set<String> signatures;

    private final boolean useCustomTab;

    private final AuthBrowserVersionRange versionRange;

    /**
     * Defines a custom browser that can be whitelisted/blacklisted for authentication.
     *
     * @param packageName the package name of the custom browser
     * @param signatures the set of signature hashes of the custom browser
     * @param useCustomTab the custom tab usage mode for the custom browser
     * @param versionRange the version range of the custom browser
     */
    public AuthBrowser(final String packageName, final Set<String> signatures,
                    final boolean useCustomTab, final AuthBrowserVersionRange versionRange) {
        this.packageName = packageName;
        this.signatures = signatures;
        this.useCustomTab = useCustomTab;
        this.versionRange = versionRange;
    }

    /**
     *
     * @return the package name of the browser
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the set of signatures for the browser
     */
    public Set<String> getSignatures() {
        return signatures;
    }

    /**
     * @return the custom tab mode usage for the browser
     */
    public boolean isUseCustomTab() {
        return useCustomTab;
    }

    /**
     * @return the version range for the browser
     */
    public AuthBrowserVersionRange getVersionRange() {
        return versionRange;
    }
}
