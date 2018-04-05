package org.aerogear.mobile.auth.authenticator.oidc;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.jose4j.jwk.JsonWebKeySet;

import android.app.Activity;
import android.content.Intent;

import org.aerogear.mobile.auth.AuthStateManager;
import org.aerogear.mobile.auth.authenticator.AbstractAuthenticator;
import org.aerogear.mobile.auth.authenticator.AuthenticateOptions;
import org.aerogear.mobile.auth.authenticator.AuthorizationServiceFactory;
import org.aerogear.mobile.auth.authenticator.DefaultAuthenticateOptions;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.JwksManager;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserPrincipalImpl;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.http.pinning.CertificatePinningCheck;
import org.aerogear.mobile.core.http.pinning.CertificatePinningCheckListener;
import org.aerogear.mobile.core.reactive.Responder;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;

/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthenticatorImpl extends AbstractAuthenticator {

    private AuthState authState;
    private AuthorizationService authService;
    private final KeycloakConfiguration keycloakConfiguration;
    private final AuthServiceConfiguration authServiceConfiguration;
    private Callback authCallback;
    private Callback logoutCallback;
    private final AuthStateManager authStateManager;
    private final JwksManager jwksManager;
    private final AuthorizationServiceFactory authorizationServiceFactory;
    private final HttpServiceModule httpModule;

    /**
     * Creates a new OIDCAuthenticatorImpl object
     *
     * @param serviceConfiguration {@link ServiceConfiguration}
     * @param authServiceConfiguration {@link AuthServiceConfiguration}
     * @param authStateManager {@link AuthStateManager}
     * @param authorizationServiceFactory {@link AuthorizationServiceFactory}
     * @param jwksManager {@link JwksManager}
     * @param httpModule {@link HttpServiceModule} Module used to make HTTP requests for
     *        authenticator.
     */
    public OIDCAuthenticatorImpl(final ServiceConfiguration serviceConfiguration,
                    final AuthServiceConfiguration authServiceConfiguration,
                    final AuthStateManager authStateManager,
                    final AuthorizationServiceFactory authorizationServiceFactory,
                    final JwksManager jwksManager, final HttpServiceModule httpModule) {
        super(serviceConfiguration);
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);
        this.authServiceConfiguration =
                        nonNull(authServiceConfiguration, "authServiceConfiguration");
        this.authorizationServiceFactory =
                        nonNull(authorizationServiceFactory, "authorizationServiceFactory");
        this.authStateManager = nonNull(authStateManager, "authStateManager");
        this.jwksManager = nonNull(jwksManager, "jwksManager");
        this.httpModule = nonNull(httpModule, "httpModule");
    }


    /**
     * Builds a new OIDCUserPrincipalImpl object after the user's credential has been authenticated
     *
     * @param authOptions the OIDC authentication options
     * @param callback the callback will be invoked with a new OIDCUserPrincipalImpl object with the
     *        user's identity that was decoded from the user's credential
     */
    @Override
    public void authenticate(final AuthenticateOptions authOptions,
                    final Callback<UserPrincipal> callback) {
        this.authCallback = nonNull(callback, "callback");
        DefaultAuthenticateOptions defaultAuthenticateOptions =
                        (DefaultAuthenticateOptions) (nonNull(authOptions, "authOptions"));
        Activity fromActivity = defaultAuthenticateOptions.getFromActivity();
        int resultCode = defaultAuthenticateOptions.getResultCode();

        if (defaultAuthenticateOptions.getSkipCertificatePinningChecks()) {
            performAuthRequest(fromActivity, resultCode);
            return;
        }
        performAuthRequestWithPreflightCheck(fromActivity, resultCode);
    }

    // Authentication code
    private void performAuthRequest(final Activity fromActivity, final int resultCode) {
        nonNull(fromActivity, "fromActivity");
        AuthorizationServiceFactory.ServiceWrapper wrapper =
                        authorizationServiceFactory.createAuthorizationService(
                                        keycloakConfiguration, authServiceConfiguration);
        authState = wrapper.getAuthState();
        authService = wrapper.getAuthorizationService();

        Intent authIntent = authService
                        .getAuthorizationRequestIntent(wrapper.getAuthorizationRequest());
        fromActivity.startActivityForResult(authIntent, resultCode);
    }

    private void performAuthRequestWithPreflightCheck(final Activity fromActivity,
                    final int resultCode) {
        CertificatePinningCheck pinningCheck = new CertificatePinningCheck(httpModule);
        pinningCheck.execute(keycloakConfiguration.getHostUrl());
        pinningCheck.attachListener(new CertificatePinningCheckListener() {
            @Override
            public void onSuccess() {
                performAuthRequest(fromActivity, resultCode);
            }

            @Override
            public void onFailure() {
                authCallback.onError(pinningCheck.getError());
            }
        });
    }

    public void handleAuthResult(final Intent intent) {
        nonNull(intent, "intent");
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        authState.update(response, error);

        if (response != null) {
            exchangeTokens(response);
        } else {
            this.authCallback.onError(error);
        }
    }

    private void exchangeTokens(final AuthorizationResponse response) {
        authService.performTokenRequest(response.createTokenExchangeRequest(),
                        (tokenResponse, exception) -> {
                            if (tokenResponse != null) {
                                authState.update(tokenResponse, exception);
                                OIDCCredentials oidcTokens = new OIDCCredentials(
                                                authState.jsonSerializeString());
                                authStateManager.save(oidcTokens);
                                try {
                                    UserIdentityParser parser = new UserIdentityParser(oidcTokens,
                                                    keycloakConfiguration);
                                    UserPrincipalImpl user = parser.parseUser();
                                    jwksManager.fetchJwks(keycloakConfiguration,
                                                    new Callback<JsonWebKeySet>() {
                                                        @Override
                                                        public void onSuccess(
                                                                        JsonWebKeySet models) {
                                                            authCallback.onSuccess(user);
                                                        }

                                                        @Override
                                                        public void onError(Throwable error) {
                                                            authCallback.onError(error);
                                                        }
                                                    });
                                } catch (Exception e) {
                                    authCallback.onError(e);
                                }
                            } else {
                                authCallback.onError(exception);
                            }
                        });
    }

    @Override
    public void logout(final UserPrincipal principal, final Callback<UserPrincipal> callback) {
        this.logoutCallback = nonNull(callback, "callback");
        nonNull(principal, "principal");

        // Get user's identity token
        String identityToken = ((UserPrincipalImpl) principal).getIdentityToken();
        // Construct the logout URL
        URL logoutUrl = parseLogoutURL(identityToken);
        // Construct and invoke logout request
        performLogout(logoutUrl);
    }

    /**
     * Performs the logout request against the parsed logout url.
     *
     * @param logoutUrl the parsed logout url {@link #parseLogoutURL(String)}.
     */
    private void performLogout(final URL logoutUrl) {
        // Using the default OkHttpServiceModule for now. This will need to be refactored for cert
        // pinning stuff
        HttpServiceModule serviceModule = new OkHttpServiceModule();

        // Creates the get request
        HttpRequest request = serviceModule.newRequest();
        request.get(logoutUrl.toString()).respondWith(new Responder<HttpResponse>() {
            @Override
            public void onResult(HttpResponse httpResponse) {
                if (httpResponse.getStatus() == HTTP_OK
                                || httpResponse.getStatus() == HTTP_MOVED_TEMP) {
                    // delete the local tokens when the session with the OIDC has been terminated
                    authStateManager.save(null);
                    logoutCallback.onSuccess();
                } else {
                    // Non HTTP 200 or 302 Status Code Returned
                    Exception error = httpResponse.getError() != null ? httpResponse.getError()
                                    : new Exception("Non HTTP 200 or 302 Status Code.");
                    MobileCore.getLogger().error(
                                    "Error Performing a Logout on the Remote OIDC Server: ", error);
                    logoutCallback.onError(error);
                }
            }

            @Override
            public void onException(Exception exception) {
                MobileCore.getLogger().error(
                                "Error Performing a Logout on the Remote OIDC Server: ", exception);
                logoutCallback.onError(exception);
            }
        });

    }

    /**
     * Constructs the logout url using the user's identity token.
     *
     * @param identityToken {@link OIDCCredentials#getIdentityToken()}
     * @return the formatted logout url.
     */
    private URL parseLogoutURL(final String identityToken) {
        String logoutRequestUri = this.keycloakConfiguration.getLogoutUrl(identityToken,
                        this.authServiceConfiguration.getRedirectUri().toString());
        try {
            return new URL(logoutRequestUri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not parse logout url");
        }
    }

    /**
     * Delete the the current tokens/authentication state.
     */
    public void deleteTokens() {
        authStateManager.clear();
    }
}
