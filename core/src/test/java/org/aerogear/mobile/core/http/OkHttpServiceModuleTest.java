package org.aerogear.mobile.core.http;

import android.support.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class OkHttpServiceModuleTest {

    @Test
    public void testType() {
        OkHttpServiceModule module = new OkHttpServiceModule();
        assertEquals("http", module.type());
    }

    @Test
    public void testGetRequestSuccessful() {
        HttpServiceModule module = new OkHttpServiceModule();

        HttpRequest request = module.newRequest();
        request.get("http://www.mocky.io/v2/5a5f74172e00006e260a8476");

        HttpResponse response = request.execute();

        assertNotNull(response);

        CountDownLatch latch = new CountDownLatch(1);
        response.onSuccess(() -> {
            latch.countDown();
        });

        response.onComplete(() -> assertEquals("{\n" +
            " \"story\": {\n" +
            "     \"title\": \"Test Title\"\n" +
            " }    \n" +
            "}", response.stringBody()));

        response.waitForCompletionAndClose();
        assertEquals(latch.getCount(), 0);
    }

    @Test
    public void testSuccessHandlerNotCalledInErrorCase() {
        HttpServiceModule module = new OkHttpServiceModule();

        HttpRequest request = module.newRequest();
        request.get("http://does.not.exist.com");

        final HttpResponse response = request.execute();
        assertNotNull(response);

        response.onSuccess(() -> {
            fail("The success handler must not be called here");
        });

        CountDownLatch latch = new CountDownLatch(1);
        response.onError(() -> {
            assertNotNull(response.getError());
            latch.countDown();
        });

        response.waitForCompletionAndClose();
        assertEquals(latch.getCount(), 0);
    }

    @Test
    public void testCompleteHandlerCalledInErrorCase() {
        HttpServiceModule module = new OkHttpServiceModule();

        HttpRequest request = module.newRequest();
        request.get("http://does.not.exist.com");

        final HttpResponse response = request.execute();
        assertNotNull(response);

        CountDownLatch latch = new CountDownLatch(1);
        response.onComplete(() -> {
            latch.countDown();
        });

        response.onError(() -> {
            assertNotNull(response.getError());
        });

        response.waitForCompletionAndClose();
        assertEquals(latch.getCount(), 0);
    }

    @Test
    public void testRedirectsShouldBeSuccessful() {
        OkHttpClient client = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

        HttpServiceModule module = new OkHttpServiceModule(client);

        HttpRequest request = module.newRequest();
        request.get("https://jigsaw.w3.org/HTTP/300/302.html");

        final HttpResponse response = request.execute();
        assertNotNull(response);

        CountDownLatch latch = new CountDownLatch(2);
        response.onComplete(() -> {
            latch.countDown();
        });

        response.onSuccess(() -> {
            latch.countDown();
        });

        response.onError(() -> {
            fail("Redirects must not be errors");
        });

        response.waitForCompletionAndClose();
        assertEquals(latch.getCount(), 0);
    }
}
