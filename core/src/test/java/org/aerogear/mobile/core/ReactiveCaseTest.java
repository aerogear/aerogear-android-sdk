package org.aerogear.mobile.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Requester;

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

        Requester.emit("Test").respondWith(responder);

        assertTrue(responder.passed);
        assertEquals("Test", responder.resultValue);

    }

    /**
     * Similar to {@link ReactiveCaseTest#synchronousConstantTest()}, but with a different value to
     * appease the linter gods.
     */
    @Test
    public void synchronousConstantTestDifferentValue() {

        TestResponder<String> responder = new TestResponder<>();

        Requester.emit("Test2").respondWith(responder);

        assertTrue(responder.passed);
        assertEquals("Test2", responder.resultValue);

    }

    @Test
    public void synchronousCallableTest() {

        TestResponder<String> responder = new TestResponder<>();

        Requester.call(() -> "Test2").respondWith(responder);

        assertTrue(responder.passed);
        assertEquals("Test2", responder.resultValue);

    }

    @Test
    public void synchronousCallableCallsOnErrorWhenExceptionTest() {

        TestResponder<Object> responder = new TestResponder<>();

        Requester.call(() -> {
            throw new RuntimeException("Catch this!");
        }).respondWith(responder);

        assertTrue(responder.failed);
        assertEquals("Catch this!", responder.errorMessage);

    }

    @Test
    public void asynchronousCallableRunsOnDifferentThread() throws InterruptedException {
        Thread testThread = Thread.currentThread();
        CountDownLatch latch = new CountDownLatch(1);
        TestResponder<Boolean> responder = new TestResponder<>(latch);

        Requester.call(() -> Thread.currentThread() != testThread)
                        .runOn(new AppExecutors().singleThreadService()).respondWith(responder);

        latch.await(1, TimeUnit.SECONDS);

        assertTrue(responder.passed);
        assertEquals(true, responder.resultValue);

    }

    @Test
    public void cancelAsynchronousRequest() throws InterruptedException {
        Thread testThread = Thread.currentThread();
        CountDownLatch latch = new CountDownLatch(1);
        TestResponder<Boolean> responder = new TestResponder<>(latch);

        Request request = Requester.call(() -> {
            Thread.sleep(Long.MAX_VALUE);// Sleep forever
            return Thread.currentThread() != testThread;
        }).runOn(new AppExecutors().singleThreadService()).respondWith(responder);

        latch.await(1, TimeUnit.SECONDS);

        request.cancel();

        latch.await(10, TimeUnit.SECONDS);

        assertTrue(responder.failed);

    }

    private static class TestResponder<T> implements Responder<T> {
        private final CountDownLatch latch;
        boolean passed = false;
        T resultValue = null;
        boolean failed;
        String errorMessage = "";
        public boolean cancelled = false;

        public TestResponder() {
            this.latch = null;
        }

        public TestResponder(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onResult(T value) {
            passed = true;
            resultValue = value;
            if (latch != null) {
                latch.countDown();
            }
        }

        @Override
        public void onException(Exception e) {
            failed = true;
            errorMessage = e.getMessage();
            if (latch != null) {
                latch.countDown();
            }
        }

    }

}
