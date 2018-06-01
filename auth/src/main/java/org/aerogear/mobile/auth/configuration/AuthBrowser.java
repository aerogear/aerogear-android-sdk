package org.aerogear.mobile.auth.configuration;

import net.openid.appauth.browser.VersionedBrowserMatcher;

import java.util.Set;

public class AuthBrowser {

    private String packageName;

    private Set<String> signatures;

    private boolean useCustomTab;

    private AuthBrowserVersionRange versionRange;

    public AuthBrowser(String packageName, Set<String> signatures, boolean useCustomTab, AuthBrowserVersionRange versionRange) {
        this.packageName = packageName;
        this.signatures = signatures;
        this.useCustomTab = useCustomTab;
        this.versionRange = versionRange;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getSignatures() {
        return signatures;
    }

    public void setSignatures(Set<String> signatures) {
        this.signatures = signatures;
    }

    public boolean isUseCustomTab() {
        return useCustomTab;
    }

    public void setUseCustomTab(boolean useCustomTab) {
        this.useCustomTab = useCustomTab;
    }

    public AuthBrowserVersionRange getVersionRange() {
        return versionRange;
    }

    public void AuthBrowserVersionRange(AuthBrowserVersionRange versionRange) {
        this.versionRange = versionRange;
    }
}
