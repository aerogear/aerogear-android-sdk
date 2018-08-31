package org.aerogear.mobile.auth;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Requester;
import org.jose4j.jwk.JsonWebKeySet;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.aerogear.mobile.auth.authenticator.AuthorizationServiceFactory;
import org.aerogear.mobile.auth.authenticator.DefaultAuthenticateOptions;
import org.aerogear.mobile.auth.authenticator.oidc.OIDCAuthenticatorImpl;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.BrowserConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.JwksManager;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.logging.Logger;

/**
 * Entry point for authenticating users.
 */
public class AuthService {
    private static final Logger LOG = MobileCore.getLogger();
    private static final String TAG = "AuthService";
    private static final String type = "keycloak";
    private KeycloakConfiguration keycloakConfiguration;

    private AuthStateManager authStateManager;

    private OIDCAuthenticatorImpl oidcAuthenticatorImpl;

    private JwksManager jwksManager;
    private final AuthHeaderProvider authHeaderProvider;

    public AuthService(final AuthServiceConfiguration authServiceConfiguration) {
        this(authServiceConfiguration, null);

    }

    public AuthService(final AuthServiceConfiguration authServiceConfiguration,
                       final BrowserConfiguration browserConfiguration) {
        nonNull(authServiceConfiguration, "authServiceConfiguration");

        ServiceConfiguration serviceConfiguration =
            MobileCore.getInstance().getServiceConfigurationByType(type);
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);
        this.authStateManager = AuthStateManager.getInstance(MobileCore.getInstance().getContext());
        this.jwksManager = new JwksManager(MobileCore.getInstance().getContext(),
            MobileCore.getInstance(), authServiceConfiguration);
        this.oidcAuthenticatorImpl = new OIDCAuthenticatorImpl(serviceConfiguration,
            authServiceConfiguration, browserConfiguration, this.authStateManager,
            new AuthorizationServiceFactory(MobileCore.getInstance().getContext()),
            jwksManager, MobileCore.getInstance().getHttpLayer());


        authHeaderProvider = new AuthHeaderProvider(this);
    }

    /**
     * Return the user that is currently logged and is still valid. Otherwise returns null
     *
     * @return the current logged in. Could be null.
     */
    public UserPrincipal currentUser() {
        UserPrincipal currentUser = null;
        JsonWebKeySet jwks = jwksManager.load(keycloakConfiguration);
        if (jwks != null) {
            OIDCCredentials currentCredentials = this.authStateManager.load();
            if ((currentCredentials.getAccessToken() != null) && !currentCredentials.isExpired()
                && currentCredentials.verifyClaims(jwks, keycloakConfiguration)
                && currentCredentials.isAuthorized()) {
                try {
                    UserIdentityParser parser = new UserIdentityParser(currentCredentials,
                        keycloakConfiguration);
                    currentUser = parser.parseUser();
                } catch (AuthenticationException ae) {
                    LOG.error(TAG, "Failed to parse user identity from credential", ae);
                    currentUser = null;
                }
            }
        }
        return currentUser;
    }


    /**
     * This will return the current user with refreshed credentials (if necessary and available).
     *
     * @return a request which will contain the current user and the most up to date credentials.  This may emit a null value.
     */
    public UserPrincipal getFreshCurrentUser() {

            UserPrincipal currentUser = null;
            JsonWebKeySet jwks = jwksManager.load(keycloakConfiguration);
            if (jwks != null) {
                OIDCCredentials currentCredentials = this.authStateManager.load();

                if (currentCredentials.getNeedsRenewal()) {
                    try {
                        return oidcAuthenticatorImpl.renew(currentCredentials);
                    } catch (Exception exception) {
                        LOG.error(TAG, "Failed to refresh token from credential", exception);
                        currentUser = null;
                    }
                }

                if ((currentCredentials.getAccessToken() != null) && !currentCredentials.isExpired()
                    && currentCredentials.verifyClaims(jwks, keycloakConfiguration)
                    && currentCredentials.isAuthorized()) {
                    try {
                        UserIdentityParser parser = new UserIdentityParser(currentCredentials,
                            keycloakConfiguration);
                        currentUser = parser.parseUser();
                    } catch (Exception ae) {
                        LOG.error(TAG, "Failed to parse user identity from credential", ae);
                        currentUser = null;
                    }
                }
            }
            return currentUser;

    }

    /**
     * Log in the user with the given authentication options. At the moment, only OIDC protocol is
     * supported. The login will be asynchronous.
     *
     * @param authOptions the authentication options
     * @param callback    the callback function that will be invoked with the user info
     */
    public void login(@NonNull final DefaultAuthenticateOptions authOptions,
                      @NonNull final Callback<UserPrincipal> callback) {
        oidcAuthenticatorImpl.authenticate(authOptions, callback);
    }

    /**
     * Delete the the current tokens/authentication state.
     */
    public void deleteTokens() {
        oidcAuthenticatorImpl.deleteTokens();
    }

    /**
     * This function should be called in the start activity's "onActivityResult" method to allow the
     * SDK to process the response from the authentication server.
     *
     * @param data The intent data that is passed to "onActivityResult"
     */
    public void handleAuthResult(@NonNull final Intent data) {
        oidcAuthenticatorImpl.handleAuthResult(data);
    }

    /**
     * Log out the given principal. The logout will be asynchronous.
     *
     * @param principal principal to be logged out
     * @param callback  the callback function to be invoked
     */
    public void logout(@NonNull final UserPrincipal principal,
                       @NonNull final Callback<UserPrincipal> callback) {
        this.oidcAuthenticatorImpl.logout(principal, callback);
    }

    /**
     * Get interface that is used to provide authentication headers. Clients can use this interface
     * to add headers into their own network layer.
     *
     * @return provider
     */
    public AuthHeaderProvider getAuthHeaderProvider() {
        return authHeaderProvider;
    }
}
