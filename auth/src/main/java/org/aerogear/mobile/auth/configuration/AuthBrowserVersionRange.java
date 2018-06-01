package org.aerogear.mobile.auth.configuration;

/**
 * Represents the version range for a browser to be used or not used during authentication. This
 * implementation is light weight wrapper around {@link net.openid.appauth.browser.VersionRange}.
 */
public class AuthBrowserVersionRange {

    private final String lowerBoundary;
    private final String upperBoundary;

    private AuthBrowserVersionRange(final String lowerBoundary, final String upperBoundary) {
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
    }

    /**
     * A version range that tries to match any version of the browser on the device.
     */
    public static final AuthBrowserVersionRange ANY = new AuthBrowserVersionRange(null, null);

    /**
     * A version range that tries to match any version equal to or between the specified versions.
     *
     * @param lowerBoundary the lower bound version of the version range
     * @param upperBoundary the upper bound version of the version range
     * @return {@link AuthBrowserVersionRange}
     */
    public static final AuthBrowserVersionRange between(final String lowerBoundary,
                    final String upperBoundary) {
        return new AuthBrowserVersionRange(lowerBoundary, upperBoundary);
    }

    /**
     * A version range that tries to match any version equal to or greater than the specified
     * version.
     *
     * @param version minimum version
     * @return {@link AuthBrowserVersionRange}
     */
    public static final AuthBrowserVersionRange atLeast(final String version) {
        return new AuthBrowserVersionRange(version, null);
    }

    /**
     * A version range that tries to match any version equal to or less than the specified version.
     *
     * @param version maximum version
     * @return {@link AuthBrowserVersionRange}
     */
    public static final AuthBrowserVersionRange atMost(final String version) {
        return new AuthBrowserVersionRange(null, version);
    }

    /**
     * @return lower boundary of the version range
     */
    public String getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * @return upper boundary of the version range
     */
    public String getUpperBoundary() {
        return upperBoundary;
    }
}
