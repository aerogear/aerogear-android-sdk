package org.aerogear.mobile.auth.configuration;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthBrowserTest {

    private static final String browserSignature =
                    "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg";
    Set<String> signatures = new HashSet<>();
    AuthBrowser authBrowser;

    @Before
    public void setup() {
        signatures.add(browserSignature);
        authBrowser = new AuthBrowser("test.package.name", signatures, true,
                        AuthBrowserVersionRange.ANY);
    }

    @Test
    public void testPackageName() {
        Assert.assertEquals("test.package.name", authBrowser.getPackageName());
    }

    @Test
    public void testSignatures() {
        Assert.assertTrue(authBrowser.getSignatures().contains(
                        "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg"));
    }

    @Test
    public void testCustomTab() {
        Assert.assertEquals(true, authBrowser.isUseCustomTab());
    }

    @Test
    public void testVersionRange() {
        Assert.assertEquals(null, authBrowser.getVersionRange().getLowerBoundary());
        Assert.assertEquals(null, authBrowser.getVersionRange().getUpperBoundary());
    }
}
