package org.aerogear.mobile.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;

public class AuthServiceTest {
    @Mock
    MobileCore mobileCore;

    @Mock
    ServiceConfiguration serviceConfiguration;

    @Mock
    Context ctx;

    @Mock
    AuthServiceConfiguration authServiceConfiguration;


    @Mock
    HttpServiceModule httpServiceModule;

    @Mock
    HttpRequest httpRequest;

    @Mock
    HttpResponse httpResponse;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        when(serviceConfiguration.getProperty(anyString())).thenReturn("dummyvalue");
        when(mobileCore.getHttpLayer()).thenReturn(httpServiceModule);
        when(httpServiceModule.newRequest()).thenReturn(httpRequest);
    }

    @Test
    public void testNotConfigured() {
        try {
            AuthService authService = new AuthService();
            authService.init(ctx, authServiceConfiguration);
            Assert.fail("Configure method has not been called, but no error has been thrown");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("configure method must be called before the init method",
                            ise.getMessage());
        }
    }

    @Test
    public void testNotInitialised() {
        try {
            AuthService authService = new AuthService();
            authService.configure(mobileCore, serviceConfiguration);
            authService.login(null, null);
            Assert.fail("init method has not been called, but no error has been thrown");
        } catch (IllegalStateException ise) {
            Assert.assertEquals(
                            "The AuthService has not been correctly initialised. Following methods needs to be called: [initialize]",
                            ise.getMessage());
        }
    }

    @Test
    public void testNotReady() {
        try {
            AuthService authService = new AuthService();
            authService.login(null, null);
            Assert.fail("init and config methods have not been called, but no error has been thrown");
        } catch (IllegalStateException ise) {
            Assert.assertEquals(
                            "The AuthService has not been correctly initialised. Following methods needs to be called: [configure, initialize]",
                            ise.getMessage());
        }
    }

    @Test
    public void testReady() {
        AuthService authService = new AuthService();
        authService.configure(mobileCore, serviceConfiguration);
        authService.init(ctx, authServiceConfiguration);
        try {
            authService.login(null, null);
        } catch (IllegalArgumentException iae) {
            // The singleThreadService is ready: it must give an error because no callback has been
            // provided
            Assert.assertEquals("Parameter 'callback' can't be null", iae.getMessage());
        }
    }
}
