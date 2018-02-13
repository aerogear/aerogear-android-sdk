package org.aerogear.mobile.auth;

import android.content.Context;
import android.content.Intent;

import org.aerogear.mobile.auth.credentials.KeyCloakWebCredentials;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.impl.OIDCAuthCodeImpl;
import org.aerogear.mobile.auth.impl.OIDCTokenAuthenticatorImpl;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.security.Principal;
import java.util.concurrent.Future;

/**
 * Entry point for authenticating users.
 */
public class AuthService implements ServiceModule {

    private AuthConfiguration authConfiguration;

    private OIDCAuthCodeImpl oidcAuthCodeImpl;
    private OIDCTokenAuthenticatorImpl oidcTokenAuthenticator;

    /**
     * Instantiates a new AuthService object
     */
    public AuthService() {}

    /**
     * Log in the user with the given credential. Flow to be used to authenticate the user is automatically
     * selected by analysing the received credentials. If the credentials are null,
     * the browser will be open asking for authentication
     *
     * The login will be asynchronous.
     *
     * @param credentials the credential
     * @return a user principal
     */
    public void login(final KeyCloakWebCredentials credentials) throws AuthenticationException {
        oidcAuthCodeImpl.authenticate(credentials);
    }

    public void login(final OIDCCredentials credentials, Callback<Principal> callback) throws AuthenticationException {
        oidcTokenAuthenticator.authenticate(credentials, callback);
    }

    public void handleAuthResult(Intent intent, Callback<Principal> callback) {
        oidcAuthCodeImpl.handleAuthResult(intent, callback);
    }

    /**
     * Log out the given principal.
     * The logout will be asynchronous.
     *
     * @param principal principal to be logged out
     */
    public void logout(Principal principal) {
        oidcTokenAuthenticator.logout(principal);
    }


    @Override
    public String type() {
        return "keycloak";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.oidcAuthCodeImpl = new OIDCAuthCodeImpl(serviceConfiguration, authConfiguration);
        this.oidcTokenAuthenticator = new OIDCTokenAuthenticatorImpl(serviceConfiguration, authConfiguration);
    }

    /**
     * Initialize the module. This should be called before any other method when using the module.
     * @param context
     */
    public void init(final Context context, final AuthConfiguration authConfiguration) {
        AuthStateManager.getInstance(context);
        this.authConfiguration = authConfiguration;
    }

    @Override
    public void destroy() {

    }

    /**
     * @return {@link #authConfiguration}
     */
    public AuthConfiguration getAuthConfiguration() {
        return authConfiguration;
    }

}
