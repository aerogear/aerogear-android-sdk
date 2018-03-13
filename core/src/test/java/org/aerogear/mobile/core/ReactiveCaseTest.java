package org.aerogear.mobile.core;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Requester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This package exists to test reactive patterns as we accomplish AG-2333.
 */
@RunWith(RobolectricTestRunner.class)
@SmallTest
public class ReactiveCaseTest {

    private MobileCore core;
    private AppExecutors executor = new AppExecutors();

    @Before
    public void setUp() {
        Application context = RuntimeEnvironment.application;

        core = MobileCore.init(context);
    }

    /**
     * Tests that a synchronous constant value is successfully emitted
     */
    @Test
    public void synchronousConstantTest() {

        final AtomicBoolean passed = new AtomicBoolean(false);
        final AtomicReference<String> testValue = new AtomicReference<>();
        Responder<String> responder = new Responder<String>() {
            @Override
            public void onSuccess(String value) {
                passed.set(true);
                testValue.set(value);
            }
        };

        Requester.emit("Test")
            .respondWith(responder);

        assertTrue(passed.get());
        assertEquals("Test", testValue.get());

    }

}
