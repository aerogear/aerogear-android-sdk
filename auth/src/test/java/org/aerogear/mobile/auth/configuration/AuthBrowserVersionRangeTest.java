package org.aerogear.mobile.auth.configuration;

import org.junit.Assert;
import org.junit.Test;

public class AuthBrowserVersionRangeTest {

    @Test
    public void testAnyVersion() {
        AuthBrowserVersionRange authBrowserVersionRange = AuthBrowserVersionRange.ANY;

        Assert.assertEquals(null, authBrowserVersionRange.getLowerBoundary());
        Assert.assertEquals(null, authBrowserVersionRange.getUpperBoundary());
    }

    @Test
    public void testBetweenVersion() {
        AuthBrowserVersionRange authBrowserVersionRange =
                        AuthBrowserVersionRange.between("1.8", "6.77.1");

        Assert.assertEquals("1.8", authBrowserVersionRange.getLowerBoundary());
        Assert.assertEquals("6.77.1", authBrowserVersionRange.getUpperBoundary());
    }

    @Test
    public void testAtLeastVersion() {
        AuthBrowserVersionRange authBrowserVersionRange = AuthBrowserVersionRange.atLeast("55");

        Assert.assertEquals("55", authBrowserVersionRange.getLowerBoundary());
        Assert.assertEquals(null, authBrowserVersionRange.getUpperBoundary());
    }

    @Test
    public void testAtMostVersion() {
        AuthBrowserVersionRange authBrowserVersionRange = AuthBrowserVersionRange.atMost("5.6");

        Assert.assertEquals(null, authBrowserVersionRange.getLowerBoundary());
        Assert.assertEquals("5.6", authBrowserVersionRange.getUpperBoundary());
    }
}
