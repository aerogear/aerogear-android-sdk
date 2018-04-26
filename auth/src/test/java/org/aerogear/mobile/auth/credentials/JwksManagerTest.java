package org.aerogear.mobile.auth.credentials;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.reactive.Request;
import org.aerogear.mobile.reactive.Responder;

import junit.framework.Assert;

@RunWith(RobolectricTestRunner.class)
public class JwksManagerTest {

    @Mock
    private Context ctx;

    @Mock
    private MobileCore mobileCore;

    @Mock
    private AuthServiceConfiguration authServiceConfiguration;

    @Mock
    private KeycloakConfiguration keycloakConfiguration;

    @Mock
    private HttpServiceModule httpServiceModule;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private Request<HttpResponse> rxHttpRequest;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private SharedPreferences sharedPrefs;

    @Mock
    private SharedPreferences.Editor sharedPrefEditor;

    private JsonWebKeySet keySet;
    private Throwable exception;

    // TEST KEY
    private static final String JWKS_CONTENT =
                    "{\"keys\":[{\"kid\":\"adSoyXNAgQxV43eqHSiRZf6hN9ytvBNQyb2fFSdCTVM\",\"kty\":\"RSA\","
                                    + "\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"kr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8Ey"
                                    + "vaFPHT1fMrFmDK03oN8Q2anh-3e894cXBXazHzzaJD-Lz1HfOOZFeInkAasxWSo8KN1-Kg-1Z7QyrPLhfcbIwfH2Stabx-3lfEMtPGws7tqWg93"
                                    + "piA8is1PwIV5_8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN--sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX-k6IWu68y_H6cicLXwS1VXPMwF"
                                    + "jDOj7cQZB7A3t4q0F-6NVL-t7UjrAAK_7V3lPB-rDwHO92iwlZw\",\"e\":\"AQAB\"}]}";

    private static final String KEYALG = "RS256";

    private static final String KEYID = "adSoyXNAgQxV43eqHSiRZf6hN9ytvBNQyb2fFSdCTVM";

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        when(ctx.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);

        when(sharedPrefs.edit()).thenReturn(sharedPrefEditor);

        when(sharedPrefEditor.putLong(anyString(), anyLong())).thenReturn(sharedPrefEditor);
        when(sharedPrefEditor.putString(anyString(), anyString())).thenReturn(sharedPrefEditor);

        when(mobileCore.getHttpLayer()).thenReturn(httpServiceModule);

        when(httpServiceModule.newRequest()).thenReturn(httpRequest);

        when(httpRequest.get(any())).thenReturn(rxHttpRequest);

        when(rxHttpRequest.respondWith(any(Responder.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Responder) invocation.getArguments()[0]).onResult(httpResponse);
                return null;
            }
        });
        when(httpResponse.getStatus()).thenReturn(200);
        when(httpResponse.stringBody()).thenReturn(JWKS_CONTENT);

        when(authServiceConfiguration.getMinTimeBetweenJwksRequests()).thenReturn(24 * 60);

        keySet = null;
        exception = null;
    }

    @Test
    public void testFetchJwks() throws InterruptedException {

        final CountDownLatch lock = new CountDownLatch(1);

        JwksManager jwksManager = new JwksManager(ctx, mobileCore, authServiceConfiguration);

        jwksManager.fetchJwks(keycloakConfiguration, new Callback<JsonWebKeySet>() {
            @Override
            public void onSuccess(JsonWebKeySet models) {
                keySet = models;
                lock.countDown();
            }

            @Override
            public void onError(Throwable error) {
                exception = error;
                lock.countDown();
            }
        });

        lock.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(keySet);
        Assert.assertNull(exception);

        List<JsonWebKey> keys = keySet.getJsonWebKeys();
        Assert.assertEquals(1, keys.size());

        JsonWebKey key = keys.get(0);
        Assert.assertEquals(KEYALG, key.getAlgorithm());
        Assert.assertEquals(KEYID, key.getKeyId());
    }

    @Test
    public void testFetchJwksIfNeeded() throws InterruptedException {
        JwksManager jwksManager = new JwksManager(ctx, mobileCore, authServiceConfiguration);

        when(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(0L,
                        System.currentTimeMillis());

        boolean fetched = jwksManager.fetchJwksIfNeeded(keycloakConfiguration, false);

        Assert.assertTrue(fetched);

        fetched = jwksManager.fetchJwksIfNeeded(keycloakConfiguration, false);

        Assert.assertFalse(fetched);
    }
}
