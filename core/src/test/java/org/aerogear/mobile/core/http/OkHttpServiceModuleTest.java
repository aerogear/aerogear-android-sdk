package org.aerogear.mobile.core.http;

import android.support.test.filters.SmallTest;

import junit.framework.Assert;

import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
    public void testGet() {
        HttpServiceModule module = new OkHttpServiceModule();

        HttpRequest request = module.newRequest();
        request.get("http://www.mocky.io/v2/5a5f74172e00006e260a8476");

        HttpResponse response = request.execute();

        assertNotNull(response);

        response.onComplete(() -> assertEquals("{\n" +
            " \"story\": {\n" +
            "     \"title\": \"Test Title\"\n" +
            " }    \n" +
            "}", response.stringBody()));

        response.waitForCompletionAndClose();
    }

    @Test
    public void testCompleteHandlerNotCalledInErrorCase() {
        HttpServiceModule module = new OkHttpServiceModule();

        HttpRequest request = module.newRequest();
        request.get("http://does.not.exist.com");

        final HttpResponse response = request.execute();
        assertNotNull(response);

        response.onComplete(() -> {
            // The complete handler must not be called here
            Assert.fail("The complete handler must not be called here");
        });

        response.onError(() -> {
            assertNotNull(response.getRequestError());
        });

        response.waitForCompletionAndClose();
    }

}
