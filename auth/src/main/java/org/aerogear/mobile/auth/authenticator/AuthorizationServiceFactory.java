package org.aerogear.mobile.auth.authenticator;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.BrowserConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

/**
 * Factory class used to create the 'openid' classes.
 */
public class AuthorizationServiceFactory {

    private final Context appContext;

    /**
     * Wrapper class for all the objects used to perform OIDC authentication through the usage of
     * the `openid` library.
     */
    public static class ServiceWrapper {
        private final AuthorizationService authorizationService;
        private final AuthState authState;
        private final AuthorizationRequest authorizationRequest;

        private ServiceWrapper(final AuthorizationService authorizationService,
                        final AuthState authState,
                        final AuthorizationRequest authorizationRequest) {
            this.authorizationService = authorizationService;
            this.authState = authState;
            this.authorizationRequest = authorizationRequest;
        }

        public AuthorizationRequest getAuthorizationRequest() {
            return authorizationRequest;
        }

        public AuthorizationService getAuthorizationService() {
            return authorizationService;
        }

        public AuthState getAuthState() {
            return authState;
        }
    }

    /**
     * Builds a new AuthorizationServiceFactory
     *
     * @param appContext the application context
     */
    public AuthorizationServiceFactory(@NonNull final Context appContext) {
        this.appContext = nonNull(appContext, "appContext").getApplicationContext();
    }

    /**
     * Creates and initializes a new {@link AuthorizationService} ready to be used for
     * authenticating with Keycloak.
     *
     * @param keycloakConfiguration configuration to be used to access keycloak
     * @param authServiceConfiguration the authentication singleThreadService configuration
     * @param browserConfiguration the configuration for the browser used during authentication/SSO
     * @return a wrapper object containing all the `openid` object used to handle the OIDC
     *         authentication
     */
    public ServiceWrapper createAuthorizationService(
                    @NonNull final KeycloakConfiguration keycloakConfiguration,
                    @NonNull final AuthServiceConfiguration authServiceConfiguration,
                    @Nullable final BrowserConfiguration browserConfiguration) {

        nonNull(keycloakConfiguration, "keycloakConfiguration");
        nonNull(authServiceConfiguration, "authServiceConfiguration");

        AuthorizationServiceConfiguration authServiceConfig = new AuthorizationServiceConfiguration(
                        keycloakConfiguration.getAuthenticationEndpoint(),
                        keycloakConfiguration.getTokenEndpoint());
        AuthState authState = new AuthState(authServiceConfig);

        AuthorizationService authService = browserConfiguration == null
                        ? new AuthorizationService(this.appContext,
                                        new AppAuthConfiguration.Builder().build())
                        : new AuthorizationService(this.appContext,
                                        browserConfiguration.getAppAuthConfig());
        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(authServiceConfig,
                        keycloakConfiguration.getResourceId(), ResponseTypeValues.CODE,
                        authServiceConfiguration.getRedirectUri())
                                        .setScopes(authServiceConfiguration.getScopes()).build();

        return new ServiceWrapper(authService, authState, authRequest);
    }
}
