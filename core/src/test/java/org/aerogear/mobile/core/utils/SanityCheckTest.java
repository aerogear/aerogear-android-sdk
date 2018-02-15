package org.aerogear.mobile.core.utils;

import android.support.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class SanityCheckTest {

    @Test
    public void testNonNull() {
        try {
            SanityCheck.nonNull(null, "test-param");
            Assert.fail("null value has not been detected");
        } catch(NullPointerException npe) {
            Assert.assertEquals("Parameter 'test-param' can't be null", npe.getMessage());
        }
    }

    @Test
    public void testNonNullWithCustomMessage() {
        try {
            SanityCheck.nonNull(null, "Test error message for %s param with custom %s string", "test-param", "custom-string");
            Assert.fail("null value has not been detected");
        } catch(NullPointerException npe) {
            Assert.assertEquals("Test error message for test-param param with custom custom-string string", npe.getMessage());
        }
    }

    @Test
    public void testNonEmpty() {
        try {
            SanityCheck.nonEmpty("     ", "empty-string");
            Assert.fail("empty value has not been detected");
        } catch(IllegalArgumentException iae) {
            Assert.assertEquals("'empty-string' can't be empty or null", iae.getMessage());
        }
    }

    @Test
    public void testNonEmptyNoTrim() {
        SanityCheck.nonEmpty("     ", "empty-string", false);
    }

    @Test
    public void testNonEmptyWithCustomMessage() {
        try {
            SanityCheck.nonEmpty("     ", "Parameter '%s' must be valorised and only spaces are not accepted", "testParam");
            Assert.fail("empty value has not been detected");
        } catch(IllegalArgumentException iae) {
            Assert.assertEquals("Parameter 'testParam' must be valorised and only spaces are not accepted", iae.getMessage());
        }
    }

    @Test
    public void testIsA() {
        SanityCheck.isA(new Integer(200), Integer.class, "test-int");

        try {
            SanityCheck.isA(new Integer(200), Long.class, "test-int");
            Assert.fail("Wrong parameter type has not been detected");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Param 'test-int' must be of type 'java.lang.Long'. 'java.lang.Integer' has been receive instead", iae.getMessage());
        }
    }

    @Test
    public void testIsAWithCustomMessage() {
        SanityCheck.isA(new Integer(200), Integer.class, "test-int");

        try {
            SanityCheck.isA(new Integer(200), Long.class, "Wrong class type for parameter %s", "test-param");
            Assert.fail("Wrong parameter type has not been detected");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Wrong class type for parameter test-param", iae.getMessage());
        }
    }
}
