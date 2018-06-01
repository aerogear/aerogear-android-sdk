package org.aerogear.mobile.auth.configuration;

//a light weight wrapper around the AppAuth's VersionRange
public class AuthBrowserVersionRange {

    public static final AuthBrowserVersionRange ANY = new AuthBrowserVersionRange();

    public String getLowerBoundry() {
        return null;
    }

    public String getUpperBoundry() {
        return null;
    }
}
