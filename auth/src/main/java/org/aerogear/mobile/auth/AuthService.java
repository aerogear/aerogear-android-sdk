package org.aerogear.mobile.auth;

import android.content.Context;
import android.content.Intent;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.authenticator.OIDCAuthenticateOptions;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.authenticator.OIDCAuthenticatorImpl;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.logging.Logger;

import java.security.Principal;

/**
 * Entry point for authenticating users.
 */
public class AuthService implements ServiceModule {

    private ServiceConfiguration serviceConfiguration;
    private KeycloakConfiguration keycloakConfiguration;
    private AuthServiceConfiguration authServiceConfiguration;

    private AuthStateManager authStateManager;

    private OIDCAuthenticatorImpl oidcAuthenticatorImpl;

    private Context appContext;
    private Logger logger;
    /**
     * Instantiates a new AuthService object
     */
    public AuthService() {}

    /**
     * Return the user that is currently logged and is still valid. Otherwise returns null
     * @return the current logged in. Could be null.
     */
    public UserPrincipal currentUser() {
        UserPrincipal currentUser = null;
        OIDCCredentials currentCredentials = this.authStateManager.load();
        if (!currentCredentials.isExpired() && currentCredentials.isAuthorized()) {
            try {
                UserIdentityParser parser = new UserIdentityParser(currentCredentials, keycloakConfiguration);
                currentUser = parser.parseUser();
            } catch (AuthenticationException ae) {
                logger.error("Failed to parse user identity from credential", ae);
            }
        }
        return currentUser;
    }

    /**
     * Log in the user with the given authentication options. At the moment, only OIDC protocol is supported.
     * The login will be asynchronous.
     *
     * @param authOptions the authentication options
     * @param callback the callback function that will be invoked with the user info
     */
    public void login(final OIDCAuthenticateOptions authOptions, final Callback<Principal> callback) {
        oidcAuthenticatorImpl.authenticate(authOptions, callback);
    }

    /**
     * This function should be called in the start activity's "onActivityResult" method to allow the SDK to process the response from the authentication server.
     * @param data The intent data that is passed to "onActivityResult"
     */
    public void handleAuthResult(final Intent data) {
        oidcAuthenticatorImpl.handleAuthResult(data);
    }

    /**
     * Log out the given principal.
     * The logout will be asynchronous.
     *
     * @param principal principal to be logged out
     */
    public void logout(final Principal principal) {
        this.oidcAuthenticatorImpl.logout(principal);
    }


    @Override
    public String type() {
        return "keycloak";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.logger = MobileCore.getLogger();
        this.serviceConfiguration = serviceConfiguration;
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);
    }

    /**
     * Initialize the module. This should be called before any other method when using the module.
     * @param context
     */
    public void init(final Context context, final AuthServiceConfiguration authServiceConfiguration) {
        this.appContext = context;
        this.authStateManager = AuthStateManager.getInstance(context);
        this.authServiceConfiguration = authServiceConfiguration;
        this.oidcAuthenticatorImpl = new OIDCAuthenticatorImpl(this.serviceConfiguration, this.authServiceConfiguration, this.appContext, this.authStateManager);
    }

    @Override
    public void destroy() {

    }
}
