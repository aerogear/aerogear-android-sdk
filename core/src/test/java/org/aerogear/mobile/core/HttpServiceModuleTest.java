package org.aerogear.mobile.core;

import android.support.test.filters.SmallTest;

import org.aerogear.mobile.core.http.HttpServiceModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class HttpServiceModuleTest {

    @Test
    public void testGet() {
        HttpServiceModule module = new HttpServiceModule();
        String response = module.get("http://www.mocky.io/v2/5a5f74172e00006e260a8476");
        assertNotNull(response);
        assertEquals("{\n" +
                " \"story\": {\n" +
                "     \"title\": \"Test Title\"\n" +
                " }    \n" +
                "}",response);
    }

}
