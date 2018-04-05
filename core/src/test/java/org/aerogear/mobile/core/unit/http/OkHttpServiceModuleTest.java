package org.aerogear.mobile.core.unit.http;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Responder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class OkHttpServiceModuleTest {

    @Test
    public void testType() {
        OkHttpServiceModule module = new OkHttpServiceModule();
        assertEquals("http", module.type());
    }

    @Test
    public void testGetRequestSuccessful() throws InterruptedException {

        final String expected = "{\n" + " \"story\": {\n"
            + "     \"title\": \"Test Title\"\n" + " }    \n" + "}";


    @Test
    public void testSuccessHandlerNotCalledInConnectionProblemCase() {

        HttpServiceModule module = new OkHttpServiceModule();
        CountDownLatch latch = new CountDownLatch(1);
        HttpRequest request = module.newRequest();
        StringBuilder responseString = new StringBuilder();

        request.get("http://www.mocky.io/v2/5a5f74172e00006e260a8476")
            .respondOn(new AppExecutors().singleThreadService())
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

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        assertEquals(expected, responseString.toString());
    }

    @Test
    public void testSuccessHandlerNotCalledInErrorCase() throws InterruptedException {
        HttpServiceModule module = new OkHttpServiceModule();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference valueRef = new AtomicReference();
        AtomicReference<Exception> errorRef = new AtomicReference();
        HttpRequest request = module.newRequest();
        request.get("http://does.not.exist.com")
            .respondOn(Executors.newSingleThreadExecutor())
            .respondWith(new Responder() {
                @Override
                public void onResult(Object value) {
                    valueRef.set(value);
                    //This won't actually stop the test because of threading, but it is helpful
                    //to let us know what is expected
                    fail("The success handler must not be called here");
                }

                @Override
                public void onException(Exception exception) {
                    errorRef.set(exception);
                    latch.countDown();
                }
            });

        assertTrue(latch.await(200, TimeUnit.SECONDS));
        assertNull(valueRef.get());
        assertNotNull(errorRef.get());
    }

    @Test
    public void testRedirectsShouldBeSuccessful() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        MockResponse redirectResponse = new MockResponse();

        // Reply with a redirect
        redirectResponse.setStatus("HTTP/1.1 302");
        server.enqueue(redirectResponse);
        server.start();

        HttpUrl url = server.url("/mockRequest");
        String urlString = url.toString();

        AtomicReference<Exception> errorRef = new AtomicReference<>();
        HttpServiceModule module = new OkHttpServiceModule();
        CountDownLatch latch = new CountDownLatch(1);
        HttpRequest request = module.newRequest();
        request.get(urlString)
            .respondOn(Executors.newSingleThreadExecutor())
            .respondWith(new Responder() {
                @Override
                public void onResult(Object value) {
                    latch.countDown();
                }

                @Override
                public void onException(Exception exception) {
                    errorRef.set(exception);
                    latch.countDown();
                }
            });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNull("Redirects should not be errors",errorRef.get());
    }
}
