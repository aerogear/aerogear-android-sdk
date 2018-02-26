package org.aerogear.mobile.security.utils;

import android.content.Context;

import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.impl.SecurityCheckResultImpl;

public class MockSecurityCheck implements SecurityCheck {

    private static final String NAME = MockSecurityCheck.class.getName();

    @Override
    public SecurityCheckResult test(Context context) {
        return new SecurityCheckResultImpl("mockTest", true);
    }

    @Override
    public String getName() {
        return null;
    }
}
