package org.aerogear.mobile.security.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.aerogear.mobile.security.DeviceCheck;
import org.aerogear.mobile.security.DeviceCheckResult;


public class DeviceCheckResultTest {

    DeviceCheckResult result;
    static final boolean RESULT_PASSED = true;

    @Mock
    private DeviceCheck deviceCheck;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        result = new DeviceCheckResultImpl(deviceCheck, RESULT_PASSED);
    }

    @Test
    public void testGetId() {
        assertEquals(deviceCheck.getId(), result.getId());
    }

    @Test
    public void testGetName() {
        assertEquals(deviceCheck.getName(), result.getName());
    }

    @Test
    public void testPassed() {
        assertTrue(result.passed());
    }
}
