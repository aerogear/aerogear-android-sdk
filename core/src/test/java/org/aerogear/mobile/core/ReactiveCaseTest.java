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

        TestResponder<String> responder = new TestResponder<>();

        Requester.emit("Test")
            .respondWith(responder);

        assertTrue(responder.passed);
        assertEquals("Test", responder.testValue);

    }

    @Test
    public void synchronousCallableTest() {

        TestResponder<String> responder = new TestResponder<>();

        Requester.call( () -> "Test2").respondWith(responder);

        assertTrue(responder.passed);
        assertEquals("Test2", responder.testValue);

    }

    @Test
    public void synchronousCallableCallsOnErrorWhenExceptionTest() {

        TestResponder responder = new TestResponder();

        Requester.call( () -> {throw new RuntimeException("Catch this!");}).respondWith(responder);

        assertTrue(responder.failed);
        assertEquals("Catch this!", responder.errorMessage);

    }

    private static class TestResponder<T> implements Responder<T> {
        boolean passed = false;
        T testValue = null;
        boolean failed;
        String errorMessage = "";

        @Override
        public void onResult(T value) {
            passed = true;
            testValue = value;
        }

        @Override
        public void onException(Exception e) {
            failed = true;
            errorMessage = e.getMessage();
        }

    }

}
