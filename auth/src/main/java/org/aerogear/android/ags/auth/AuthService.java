package org.aerogear.android.ags.auth;

import android.content.Context;

import net.openid.appauth.AuthState;

import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.impl.OIDCAuthCodeImpl;
import org.aerogear.android.ags.auth.impl.OIDCTokenAuthenticatorImpl;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.security.Principal;
import java.util.concurrent.Future;

/**
 * Entry point for authenticating users.
 */
public class AuthService implements ServiceModule {

    private AuthenticationChain authenticatorChain;

    /**
     * Instantiates a new AuthService object
     */
    public AuthService() {}

    private void configureDefaultAuthenticationChain(final AuthenticationChain authenticationChain) {

    }

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
    public Future<Principal> login(final ICredential credentials) {
        return authenticatorChain.authenticate(credentials);
    }

    /**
     * Log out the given principal.
     * The logout will be asynchronous.
     *
     * @param principal principal to be logged out
     */
    public Future<Void> logout(Principal principal) {
        if (principal instanceof AbstractPrincipal) {
            return authenticatorChain.logout(principal);
        }

        throw new IllegalArgumentException("Unknown principal type " + principal.getClass().getName());
    }

    public void setAuthenticatorChain(AuthenticationChain newChain) {
        this.authenticatorChain = newChain;
    }

    @Override
    public String type() {
        return "keycloak";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.authenticatorChain = AuthenticationChain
            .newChain()
            .with(new OIDCTokenAuthenticatorImpl(serviceConfiguration))
            .with(new OIDCAuthCodeImpl(serviceConfiguration))
            .build();
    }

    /**
     * Initialize the module. This should be called before any other method when using the module.
     * @param context
     */
    public void init(final Context context) {
        AuthStateManager.getInstance(context);
    }

    @Override
    public void destroy() {

    }
    
    /**
     * @return a built AuthConfiguration object.
     */
    public AuthConfiguration getAuthConfiguration() {
        return new AuthConfiguration
            .Builder()
            .redirectUri("Not yet implemented - will be obtained from developer provided config")
            .build();
    }

}
