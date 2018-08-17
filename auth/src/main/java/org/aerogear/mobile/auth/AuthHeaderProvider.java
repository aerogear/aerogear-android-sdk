package org.aerogear.mobile.auth;

import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.http.interceptors.HeaderProvider;

import java.util.Collections;
import java.util.Map;

/**
 * Default provider class that will build headers based on currently logged in user access token
 */
public class AuthHeaderProvider implements HeaderProvider {

    public static final String TYPE = "Bearer ";
    public static final String HEADER_KEY = "Authorization";

    private AuthService authService;

    public AuthHeaderProvider(AuthService authService) {
        this.authService = authService;
    }

    /***
     * Build auth header if user is logged in and has valid token.
     * @return Auth header in map
     */
    @Override
    public Map<String, String> getHeaders() {
        UserPrincipal user = authService.currentUser();
        if (user != null && user.getAccessToken() != null) {
            String accessToken = user.getAccessToken();
            return Collections.singletonMap(HEADER_KEY, TYPE + accessToken);
        }
        return Collections.emptyMap();
    }
}