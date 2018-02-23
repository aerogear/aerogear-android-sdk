package org.aerogear.mobile.security;

public interface Callback extends org.aerogear.mobile.auth.Callback<SecurityCheckResult> {
    public void onComplete();
}
