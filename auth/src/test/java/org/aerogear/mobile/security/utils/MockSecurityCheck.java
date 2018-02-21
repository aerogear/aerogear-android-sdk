package org.aerogear.mobile.security.utils;

import android.content.Context;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

public class MockSecurityCheck implements SecurityCheck {
    @Override
    public SecurityCheckResult test(Context context) {
        return new SecurityCheckResultImpl("mockTest", true);
    }
}
