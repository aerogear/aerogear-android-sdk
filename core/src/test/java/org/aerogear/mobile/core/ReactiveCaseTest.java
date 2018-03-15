package org.aerogear.mobile.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void asynchronousCallableRunsOnDifferentThreadTest() throws InterruptedException {
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

    /**
     * Our operating principle is that responders will reinvoke any underlying calculations when
     * they are attached unless cache() is called on the request
     */
    @Test
    public void multipleRespondersRerunCallTest() {
        AtomicInteger counter = new AtomicInteger(0);
        TestResponder<Integer> responder = new TestResponder<>();
        TestResponder<Integer> responder2 = new TestResponder<>();

        Requester.call(() -> counter.getAndIncrement()).respondWith(responder)
                        .respondWith(responder2);

        assertTrue(responder2.passed);
        assertEquals(0, (int) responder.resultValue);
        assertEquals(1, (int) responder2.resultValue);

    }

    /**
     * Our operating principle is that responders will reinvoke any underlying calculations when
     * they are attached unless cache() is called on the request.
     */
    @Test
    public void multipleRespondersGetCachedValueTest() {
        AtomicInteger counter = new AtomicInteger(0);
        TestResponder<Integer> responder = new TestResponder<>();
        TestResponder<Integer> responder2 = new TestResponder<>();

        Requester.call(() -> counter.getAndIncrement()).cache().respondWith(responder)
                        .respondWith(responder2);

        assertTrue(responder2.passed);
        assertEquals(0, (int) responder.resultValue);
        assertEquals(0, (int) responder2.resultValue);

    }

    /**
     * Ok, so several operations and delegates have been coded. This test is a bit of a logical
     * stress test for interactions between multiple responders, caches, and a very slow requester.
     */
    @Test
    public void gonzoTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(4);
        TestResponder<Integer> getsValue0OnThisThread = new TestResponder<>(latch);
        TestResponder<Integer> getsValue1OnThisThread = new TestResponder<>(latch);
        TestResponder<Integer> getsValue2OnThisThreadAfterCache = new TestResponder<>(latch);
        TestResponder<Integer> getsValue2OnAnotherThreadAfterCache = new TestResponder<>(latch);


        Requester.call(() -> {
            Thread.sleep(1000); // $DELAY
            return counter.getAndIncrement();
        }).respondWith(getsValue0OnThisThread).respondWith(getsValue1OnThisThread).cache()
                        .respondWith(getsValue2OnThisThreadAfterCache)
                        .runOn(Executors.newSingleThreadExecutor())
                        .respondWith(getsValue2OnAnotherThreadAfterCache);

        boolean timeout = latch.await(3100, TimeUnit.MILLISECONDS);// $DELAY should only be called
                                                                   // three times
        // Therefore if it is called more the lock
        // will timeout

        assertTrue(getsValue1OnThisThread.passed);
        assertTrue(timeout);
        assertEquals(0, (int) getsValue0OnThisThread.resultValue);
        assertEquals(1, (int) getsValue1OnThisThread.resultValue);
        assertEquals(2, (int) getsValue2OnThisThreadAfterCache.resultValue);
        assertEquals(2, (int) getsValue2OnAnotherThreadAfterCache.resultValue);

    }

    @Test
    public void deepCacheOnlyCallsRequestOnceTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(4);
        TestResponder<Integer> first = new TestResponder<>(latch);
        TestResponder<Integer> second = new TestResponder<>(latch);

        Requester.call(() -> {
            Thread.sleep(1000); // $DELAY
            return counter.getAndIncrement();
        }).cache().cache().cache().cache().runOn(Executors.newSingleThreadExecutor()).cache()
                        .cache().cache().cache().respondWith(first).respondWith(second);

        boolean timeout = latch.await(1100, TimeUnit.MILLISECONDS);// $DELAY should only be called
                                                                   // once

        assertTrue(second.passed);
        assertTrue(timeout);
        assertEquals(0, (int) first.resultValue);
        assertEquals(0, (int) second.resultValue);
        assertEquals(1, counter.get());
    }


    @Test
    public void crashingRespondersDoNotCrashRequestTest() {
        TestResponder<String> testResponder = new TestResponder<>();

        Requester.emit("Test").respondWith(new TestResponder<String>() {
            @Override
            public void onResult(String value) {
                throw new RuntimeException("Contrived exception");
            }
        }).respondWith(testResponder);

        assertTrue(testResponder.passed);
        assertEquals("Test", testResponder.resultValue);


    }

    @Test
    public void disconnectResponderTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        TestResponder<Integer> stayConnectedResponder = new TestResponder<>(latch);
        stayConnectedResponder.name = "stay";
        TestResponder<Integer> disconnectResponder = new TestResponder<>(latch);
        disconnectResponder.name = "go";

        Request<Integer> request = Requester.call(() -> {
            Thread.sleep(1000);
            return counter.getAndIncrement();
        }).runOn(Executors.newSingleThreadExecutor()).respondWith(stayConnectedResponder)
                        .respondWith(disconnectResponder);

        request.disconnect(disconnectResponder);

        /*
         * We have two responders and a request which has a 1 second delay. While the first
         * responder is loading we are going to disconnect the second responder. This means that the
         * latch will not be called and we expect the await to timeout. When await times out without
         * completing it returns false. This is the result we expect
         */
        latch.await(3, TimeUnit.SECONDS);


        assertTrue(stayConnectedResponder.passed);
        assertFalse(disconnectResponder.passed);
        assertFalse(disconnectResponder.failed);
    }

    @Test
    public void respondOnThreadTest() {
        fail("Not implemented");
    }

    private static class TestResponder<T> implements Responder<T> {
        private final CountDownLatch latch;

        boolean passed = false;
        T resultValue = null;
        boolean failed;
        String errorMessage = "";
        String name = "";

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

        @Override
        public String toString() {
            return "TestResponder{" + "name='" + name + '\'' + '}';
        }
    }

}
