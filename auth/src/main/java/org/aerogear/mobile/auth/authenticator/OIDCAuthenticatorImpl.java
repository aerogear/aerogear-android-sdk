package org.aerogear.mobile.auth.authenticator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.VersionedBrowserMatcher;

import org.aerogear.mobile.auth.AuthStateManager;
import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.ConnectionBuilderForTesting;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.JwksManager;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserPrincipalImpl;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.jose4j.jwk.JsonWebKeySet;

import java.net.MalformedURLException;
import java.net.URL;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthenticatorImpl extends AbstractAuthenticator {

    private final static String SCOPE_OPENID = "openid";

    private AuthState authState;

    private AuthorizationService authService;

    private final KeycloakConfiguration keycloakConfiguration;
    private final AuthServiceConfiguration authServiceConfiguration;

    private final Context appContext;

    private Callback authCallback;

    private final AuthStateManager authStateManager;
    private final JwksManager jwksManager;

    /**
     * Creates a new OIDCAuthenticatorImpl object
     *
     * @param serviceConfiguration {@link ServiceConfiguration}
     * @param authServiceConfiguration {@link AuthServiceConfiguration}
     */
    public OIDCAuthenticatorImpl(final ServiceConfiguration serviceConfiguration, final AuthServiceConfiguration authServiceConfiguration, final Context context, final AuthStateManager authStateManager, final JwksManager jwksManager) {
        super(serviceConfiguration);
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);
        this.authServiceConfiguration = nonNull(authServiceConfiguration, "authServiceConfiguration");
        this.appContext = nonNull(context, "context");
        this.authStateManager = nonNull(authStateManager, "authStateManager");
        this.jwksManager = nonNull(jwksManager, "jwksManager");
    }

    /**
     * Builds a new OIDCUserPrincipalImpl object after the user's credential has been authenticated
     *
     * @param authOptions the OIDC authentication options
     * @param callback the callback will be invoked with a new OIDCUserPrincipalImpl object with the user's identity that was decoded from the user's credential
     */
    @Override
    public void authenticate(final AuthenticateOptions authOptions, final Callback<UserPrincipal> callback) {
        this.authCallback = nonNull(callback, "callback");
        OIDCAuthenticateOptions oidcAuthenticateOptions = (OIDCAuthenticateOptions) (nonNull(authOptions, "authOptions"));
        performAuthRequest(oidcAuthenticateOptions.getFromActivity(), oidcAuthenticateOptions.getResultCode());
    }

    // Authentication code
    private void performAuthRequest(final Activity fromActivity, final int resultCode) {
        nonNull(fromActivity, "fromActivity");

        AuthorizationServiceConfiguration authServiceConfig = new AuthorizationServiceConfiguration(
            this.keycloakConfiguration.getAuthenticationEndpoint(),
            this.keycloakConfiguration.getTokenEndpoint()
        );
        this.authState = new AuthState(authServiceConfig);

        AppAuthConfiguration.Builder appAuthConfigurationBuilder = new AppAuthConfiguration.Builder()
            .setBrowserMatcher(new BrowserBlacklist(
                VersionedBrowserMatcher.CHROME_CUSTOM_TAB));

        if (this.authServiceConfiguration.isAllowSelfSignedCertificate()) {
            appAuthConfigurationBuilder.setConnectionBuilder(new ConnectionBuilderForTesting());
        }

        AppAuthConfiguration appAuthConfig = appAuthConfigurationBuilder.build();

        this.authService = new AuthorizationService(this.appContext, appAuthConfig);
        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
            authServiceConfig,
            this.keycloakConfiguration.getClientId(),
            ResponseTypeValues.CODE,
            this.authServiceConfiguration.getRedirectUri()
        ).setScopes(SCOPE_OPENID).build();

        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        fromActivity.startActivityForResult(authIntent, resultCode);
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
        authService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                if (tokenResponse != null) {
                    authState.update(tokenResponse, exception);
                    OIDCCredentials oidcTokens = new OIDCCredentials(authState.jsonSerializeString(), null);
                    authStateManager.save(oidcTokens);
                    try {
                        UserIdentityParser parser = new UserIdentityParser(oidcTokens, keycloakConfiguration);
                        UserPrincipalImpl user = parser.parseUser();
                        jwksManager.fetchJwks(keycloakConfiguration, new Callback<JsonWebKeySet>() {
                            @Override
                            public void onSuccess(JsonWebKeySet models) {
                                authCallback.onSuccess(user);
                            }

                            @Override
                            public void onError(Throwable error) {
                                authCallback.onError(error);
                            }
                        });
                    } catch(Exception e) {
                        authCallback.onError(e);
                    }
                } else {
                    authCallback.onError(exception);
                }
            }
        });
    }

    @Override
    public void logout(final UserPrincipal principal) {
        nonNull(principal, "principal");

        // Get user's identity token
        String identityToken = ((UserPrincipalImpl)principal).getIdentityToken();
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
        // Using the default OkHttpServiceModule for now. This will need to be refactored for cert pinning stuff
        HttpServiceModule serviceModule = new OkHttpServiceModule();

        // Creates the get request
        HttpRequest request = serviceModule.newRequest();
        request.get(logoutUrl.toString());

        // Creates and handles the response
        new AppExecutors().networkThread().execute(new Runnable() {
            @Override
            public void run() {
                request.execute();
            }
        });
        //it doesn't matter if the logout request is successful or not, we should always delete the local tokens
        //the remote session should be timed out eventually
        authStateManager.save(null);
    }

    /**
     * Constructs the logout url using the user's idenity token.
     *
     * @param identityToken {@link OIDCCredentials#getIdentityToken()}
     * @return the formatted logout url.
     */
    private URL parseLogoutURL(final String identityToken) {
        String logoutRequestUri = this.keycloakConfiguration.getLogoutUrl(identityToken, this.authServiceConfiguration.getRedirectUri().toString());
        URL url = null;
        try {
            url = new URL(logoutRequestUri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not parse logout url");
        }
        return url;
    }
}
