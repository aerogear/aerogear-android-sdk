package org.aerogear.mobile.core.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.categories.UnitTest;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.helper.AsyncWithCallback;
import org.aerogear.mobile.core.helper.HangsAfterCleanup;
import org.aerogear.mobile.core.helper.TestResponder;
import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Requester;
import org.aerogear.mobile.core.reactive.Responder;

/**
 * This package exists to test reactive patterns as we accomplish AG-2333.
 */
@RunWith(AeroGearTestRunner.class)
@SmallTest
@Category(UnitTest.class)
public class AsyncReactiveCaseTest {

    private AppExecutors executor = new AppExecutors();

    @Before
    public void setUp() {
        Application context = RuntimeEnvironment.application;

        MobileCore.init(context);
    }

    @Test
    public void asynchronousCallableCallsOnErrorWhenExceptionTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TestResponder<Object> responder = new TestResponder<>(latch);

        Requester.call((Responder<Object> requestCallback) -> {

            new AsyncWithCallback(new RuntimeException("Catch this!"))
                            .execute(new Callback<Object>() {

                                @Override
                                public void onSuccess(Object models) {

                                    requestCallback.onResult(models);
                                }

                                @Override
                                public void onError(Throwable error) {
                                    requestCallback.onException(new Exception(error));
                                }

                            });
        }).respondWith(responder);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(responder.failed);

    }

    /**
     * Our operating principle is that responders will reinvoke any underlying calculations when
     * they are attached unless cache() is called on the request
     */
    @Test
    public void multipleRespondersRerunCallTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);
        TestResponder<Integer> responder = new TestResponder<>(latch);
        TestResponder<Integer> responder2 = new TestResponder<>(latch);

        Requester.call((Responder<Integer> requestCallback) -> {

            new AsyncWithCallback(() -> counter.getAndIncrement()).execute(new Callback<Integer>() {

                @Override
                public void onSuccess(Integer models) {

                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }).respondWith(responder).respondWith(responder2);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(responder2.passed);
        assertEquals(0, (int) responder.resultValue);
        assertEquals(1, (int) responder2.resultValue);

    }

    /**
     * Our operating principle is that responders will reinvoke any underlying calculations when
     * they are attached unless cache() is called on the request.
     */
    @Test
    public void multipleRespondersGetCachedValueTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(3);
        TestResponder<Integer> responder = new TestResponder<>(latch);
        TestResponder<Integer> responder2 = new TestResponder<>(latch);


        Requester.call((Responder<Integer> requestCallback) -> {

            new AsyncWithCallback(() -> {
                latch.countDown();
                return counter.getAndIncrement();
            }).execute(new Callback<Integer>() {

                @Override
                public void onSuccess(Integer models) {

                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }).requestOn(Executors.newSingleThreadExecutor())
                        .respondOn(Executors.newSingleThreadExecutor()).cache()
                        .respondWith(responder).respondWith(responder2);

        assertTrue(latch.await(4, TimeUnit.SECONDS));
        assertTrue(responder2.passed);
        assertEquals(0, (int) responder.resultValue);
        assertEquals(0, (int) responder2.resultValue);
        assertEquals(1, (int) counter.get());

    }

    @Test
    public void deepCacheOnlyCallsRequestOnceTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        TestResponder<Integer> first = new TestResponder<>(latch);
        TestResponder<Integer> second = new TestResponder<>(latch);

        Requester.call((Responder<Integer> requestCallback) -> {

            new AsyncWithCallback(() -> counter.getAndIncrement()).execute(new Callback<Integer>() {

                @Override
                public void onSuccess(Integer models) {

                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }).cache().cache().cache().cache().requestOn(executor.singleThreadService()).cache().cache()
                        .cache().cache().respondWith(first).respondWith(second);

        latch.await(2000, TimeUnit.MILLISECONDS);
        assertTrue(second.passed);
        assertEquals(0, (int) first.resultValue);
        assertEquals(0, (int) second.resultValue);
        assertEquals(1, counter.get());
    }


    @Test
    public void disconnectResponderTest() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        TestResponder<Integer> stayConnectedResponder = new TestResponder<>(latch);
        stayConnectedResponder.name = "stay";
        TestResponder<Integer> disconnectResponder = new TestResponder<>(latch);
        disconnectResponder.name = "go";

        Request<Integer> request = Requester.call((Responder<Integer> requestCallback) -> {

            new AsyncWithCallback(() -> {
                Thread.sleep(1000);
                return counter.getAndIncrement();
            }).execute(new Callback<Integer>() {

                @Override
                public void onSuccess(Integer models) {

                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }).requestOn(executor.singleThreadService()).respondWith(stayConnectedResponder)
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

    //
    /**
     * Test that a basic map of turning a integer into a string works.
     */
    @Test
    public void basicMapTest() throws InterruptedException {
        final Integer originalValue = 8675309;
        final String expectedValue = "8675309";
        CountDownLatch latch = new CountDownLatch(1);
        TestResponder<String> responder = new TestResponder<>(latch);
        Requester.call((Responder<Integer> requestCallback) -> {

            new AsyncWithCallback(() -> originalValue).execute(new Callback<Integer>() {

                @Override
                public void onSuccess(Integer models) {
                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }).map((val) -> val.toString()).respondWith(responder);

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(expectedValue, responder.resultValue);
    }

    /**
     * Test that a cleanup function is called when the request and response are on different
     * threads.
     */
    @Test
    public void cleanupThreadingTest() throws InterruptedException {
        HangsAfterCleanup closeThis = new HangsAfterCleanup();
        CountDownLatch latch = new CountDownLatch(2);
        Requester.call((Responder<HangsAfterCleanup> requestCallback) -> {

            new AsyncWithCallback(() -> {
                return closeThis;
            }).execute(new Callback<HangsAfterCleanup>() {

                @Override
                public void onSuccess(HangsAfterCleanup models) {

                    requestCallback.onResult(models);
                }

                @Override
                public void onError(Throwable error) {
                    requestCallback.onException(new Exception(error));
                }

            });
        }, () -> {
            closeThis.close();
            latch.countDown();
        }).requestOn(Executors.newSingleThreadExecutor())
                        .respondWith(new Responder<HangsAfterCleanup>() {
                            @Override
                            public void onResult(HangsAfterCleanup value) {
                                value.get();
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertTrue(closeThis.closed);
    }

    /**
     * This method will test the ability of the Rx APis to transform a callback style API into a RX
     * style api.
     * <p>
     * In Apollo for instance queries are returned via a callback :
     * <p>
     * apolloClient.query(query).execute(mycallback);
     * <p>
     * However we would like to wrap the code something like this
     * <p>
     * Request.fromCallback((requestCallback)->{ apolloClient.query(query).execute(requestCallback);
     * })...
     * <p>
     * This way request callback is hooked into the callback infrastructure.
     */
    @Test
    public void testFromCallback() throws InterruptedException {

        AtomicReference<String> resultRef = new AtomicReference<>();

        new AppExecutors().singleThreadService().execute(() -> {
            Requester.call((Responder<String> requestCallback) -> {
                new AsyncWithCallback("Hello World!").execute(new Callback<String>() {

                    @Override
                    public void onSuccess(String models) {
                        requestCallback.onResult(models);
                    }

                    @Override
                    public void onError(Throwable error) {
                        // ignore
                    }

                });
            }).respondWith(new Responder<String>() {
                @Override
                public void onResult(String value) {
                    resultRef.set(value);
                }

                @Override
                public void onException(Exception exception) {
                    // ignore
                }
            }

            );

        });
        Thread.sleep(2000);

    }

}
