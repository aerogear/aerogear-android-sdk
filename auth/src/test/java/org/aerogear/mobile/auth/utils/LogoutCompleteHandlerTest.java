package org.aerogear.mobile.auth.utils;

import org.aerogear.mobile.auth.AuthStateManager;
import org.aerogear.mobile.auth.utils.LogoutCompleteHandler;
import org.aerogear.mobile.core.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class LogoutCompleteHandlerTest {

    @Mock
    private HttpResponse response;
    @Mock
    private AuthStateManager authStateManager;

    private LogoutCompleteHandler logoutCompleteHandler;

    private final Integer HTTP_OK = 200;
    private final Integer HTTP_BAD_REQUEST = 400;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        logoutCompleteHandler = new LogoutCompleteHandler(response, authStateManager);
    }

    @Test
    public void testLogoutResponse_Success() {
        when(response.getStatus()).thenReturn(HTTP_OK);

        logoutCompleteHandler.run();

        verify(authStateManager, times(1)).save(null);
    }

    @Test
    public void testLogoutResponse_Failure() {
        when(response.getStatus()).thenReturn(HTTP_BAD_REQUEST);

        logoutCompleteHandler.run();

        verify(authStateManager, times(0)).save(null);
    }

}
