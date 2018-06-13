package org.aerogear.mobile.core.unit.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import com.google.common.base.Strings;

import android.app.Application;
import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.AeroGearTestRunner;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpResponse;
import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Responder;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * This class contains tests for the reactive rewrite of Http Requests and responses.
 */

@RunWith(AeroGearTestRunner.class)
@SmallTest
public class ReactiveHTTPTest {

    private AppExecutors executor = new AppExecutors();

    @Before
    public void setUp() {
        Application context = RuntimeEnvironment.application;
        MobileCore.init(context);
    }

    /**
     * Test a basic HTTP Get request. The Responder should be passed a string in this test
     */
    @Test
    public void httpGetStringValueTest() throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse = "Hello World!";
        StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response = new MockResponse();
        response.setBody(expectedResponse);
        response.setStatus("HTTP/1.1 200");
        webServer.enqueue(response);

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
                        .respondOn(executor.singleThreadService())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                System.out.println("OnResult On" + Thread.currentThread());
                                responseString.append(value.stringBody());
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });
        System.out.println("test wait on " + Thread.currentThread());
        latch.await(10, TimeUnit.SECONDS);

        // cleanupMocks
        webServer.shutdown();

        assertEquals(expectedResponse, responseString.toString());

    }

    @Test
    public void httpGetStringValueTest2() throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse1 = "Hello World!";
        final String expectedResponse2 = "Ola World!";

        StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(2);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response1 = new MockResponse();
        response1.setBody(expectedResponse1);
        response1.setStatus("HTTP/1.1 200");
        webServer.enqueue(response1);

        MockResponse response2 = new MockResponse();
        response2.setBody(expectedResponse2);
        response2.setStatus("HTTP/1.1 200");
        webServer.enqueue(response2);

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
            .respondOn(executor.singleThreadService())
            .respondWith(new Responder<HttpResponse>() {
                @Override
                public void onResult(HttpResponse value) {
                    System.out.println("OnResult 1 On" + Thread.currentThread());
                    responseString.append(value.stringBody());
                    latch.countDown();
                }

                @Override
                public void onException(Exception exception) {
                    latch.countDown();
                }
            });

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
            .respondOn(executor.singleThreadService())
            .respondWith(new Responder<HttpResponse>() {
                @Override
                public void onResult(HttpResponse value) {
                    System.out.println("OnResult 2 On" + Thread.currentThread());
                    responseString.append(value.stringBody());
                    latch.countDown();
                }

                @Override
                public void onException(Exception exception) {
                    latch.countDown();
                }
            });

        System.out.println("test wait on " + Thread.currentThread());
        latch.await(10, TimeUnit.SECONDS);

        // cleanupMocks
        webServer.shutdown();

        assertEquals(expectedResponse1 + expectedResponse2, responseString.toString());

    }

    /**
     * A HTTP String response should close itself.
     * <p>
     * This means that calls to {@link HttpResponse#waitForCompletionAndClose()} are automatic
     */
    @Test
    public void httpStringRequestsCloseAutomaticallyTest()
                    throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse = "Hello World!";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<HttpResponse> responseReference = new AtomicReference<>();
        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response = new MockResponse();
        response.setBody(expectedResponse);
        response.setStatus("HTTP/1.1 200");
        webServer.enqueue(response);

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                responseReference.set(value);
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });

        latch.await(1, TimeUnit.SECONDS);

        // cleanupMocks
        webServer.shutdown();

        assertTrue(((OkHttpResponse) responseReference.get()).isClosed());
    }

    /**
     * Test a http GET request that returns an inputstream instead of a String value
     */
    @Test
    public void httpGetInputStreamValueTest() throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse = "Hello World!";
        StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response = new MockResponse();
        response.setBody(expectedResponse);
        response.setStatus("HTTP/1.1 200");
        webServer.enqueue(response);

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                try (InputStream bodyStream = value.streamBody()) {
                                    int readValue = bodyStream.read();
                                    while (readValue != -1) {
                                        responseString.append((char) readValue);
                                        readValue = bodyStream.read();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });

        latch.await(1, TimeUnit.SECONDS);

        // cleanupMocks
        webServer.shutdown();

        assertEquals(expectedResponse, responseString.toString());
    }

    /**
     * Test a long HTTP request cna be cancelled.
     */
    @Test
    public void httpCancelTest() throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse = "Response is no expected";
        StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response = new MockResponse();
        response.setHeadersDelay(5, TimeUnit.SECONDS);
        response.setBodyDelay(5, TimeUnit.SECONDS);
        response.setBody(expectedResponse);
        response.setStatus("HTTP/1.1 200");
        webServer.enqueue(response);

        // Actual test
        Request request = MobileCore.getInstance().getHttpLayer().newRequest()
                        .get(webServer.url("/test").toString())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                try (InputStream bodyStream = value.streamBody()) {
                                    int readValue = bodyStream.read();
                                    while (readValue != -1) {
                                        responseString.append((char) readValue);
                                        readValue = bodyStream.read();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });


        latch.await(100, TimeUnit.MILLISECONDS);
        request.cancel();
        assertTrue(latch.await(20, TimeUnit.SECONDS));


        // cleanupMocks
        webServer.shutdown();

        assertEquals("", responseString.toString());
    }

    /**
     * Test a basic HTTP Get request. The Responder should be passed a string in this test
     */
    @Test
    public void httpPostStringValueTest() throws IOException, InterruptedException {
        // Setup Test References
        final String expectedResponse = "Hello World!";
        StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();

        MockResponse response = new MockResponse();
        response.setBody(expectedResponse);
        response.setStatus("HTTP/1.1 200");
        webServer.enqueue(response);

        // Actual test
        MobileCore.getInstance().getHttpLayer().newRequest().get(webServer.url("/test").toString())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                responseString.append(value.stringBody());
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });

        latch.await(1, TimeUnit.SECONDS);

        // cleanupMocks
        webServer.shutdown();

        assertEquals(expectedResponse, responseString.toString());

    }

    /**
     * This test will test that a Request which emits a Request that uses HTTP is properly called.
     * <p>
     * This case would be if you need the output of a http request to be fed into a second http
     * request but want to wrap the whole operation in a single request. This is the case with
     * PushService#register.
     */
    @Test
    public void testFlatMapHttpResults() throws IOException, InterruptedException {
        final String expectedResponse = "Hello World!";
        final String firstResponse = "Hello";
        final String secondResponse = " World!";
        final StringBuilder responseString = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        // Setup mocks
        MockWebServer webServer = new MockWebServer();
        webServer.start();


        webServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath();
                if (Strings.isNullOrEmpty(request.getPath()) || path.equals("/")) {
                    MockResponse response = new MockResponse();
                    response.setBody(firstResponse); // No path? return Hello
                    response.setStatus("HTTP/1.1 200");
                    return response;
                } else {
                    // the second request will append the output of the first to the path of the
                    // request,
                    MockResponse response = new MockResponse();
                    response.setBody(request.getPath().replace("/", "") + secondResponse);
                    response.setStatus("HTTP/1.1 200");
                    return response;
                }

            }
        });


        // Actual test
        HttpServiceModule http = MobileCore.getInstance().getHttpLayer();

        http.newRequest().get(webServer.url("").toString())
                        .requestMap((httpResponse) -> http.newRequest().get(
                                        webServer.url("" + httpResponse.stringBody()).toString()))
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                responseString.append(value.stringBody());
                                latch.countDown();
                            }

                            @Override
                            public void onException(Exception exception) {
                                latch.countDown();
                            }
                        });

        latch.await(100, TimeUnit.SECONDS);
        assertEquals(expectedResponse, responseString.toString());
    }

}
