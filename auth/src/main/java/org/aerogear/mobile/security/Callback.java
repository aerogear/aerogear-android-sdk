package org.aerogear.mobile.security;

public interface Callback {
    public void onSecurityCheckExecuted(SecurityCheckResult result);
    public void onComplete();
}
