package org.aerogear.mobile.auth;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.aerogear.mobile.auth.utils.CertificatePinningCheck;
import org.jose4j.jwk.JsonWebKeySet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.aerogear.mobile.auth.authenticator.AuthorizationServiceFactory;
import org.aerogear.mobile.auth.authenticator.DefaultAuthenticateOptions;
import org.aerogear.mobile.auth.authenticator.oidc.OIDCAuthenticatorImpl;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.JwksManager;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.logging.Logger;

/**
 * Entry point for authenticating users.
 */
public class AuthService implements ServiceModule {
    private final static Logger LOG = MobileCore.getLogger();
    private final static String TAG = "AuthService";
    private ServiceConfiguration serviceConfiguration;
    private KeycloakConfiguration keycloakConfiguration;
    private AuthServiceConfiguration authServiceConfiguration;

    private AuthStateManager authStateManager;

    private OIDCAuthenticatorImpl oidcAuthenticatorImpl;

    private Context appContext;
    private JwksManager jwksManager;

    private MobileCore mobileCore;

    /**
     * Enumeration of all the steps that must be executed to make this singleThreadService ready
     */
    private enum STEP {
        /**
         * This steps is related to the 'configure' method
         */
        CONFIGURED("configure"),
        /**
         * This step is related to the 'initialize' method
         */
        INITIALIZED("initialize");

        /**
         * The mothod that must be invoked to perform the required step.
         */
        String methodName;

        STEP(final String methodName) {
            this.methodName = methodName;
        }
    };

    /**
     * Stores the list of executed initialisation steps. When started, all steps must be executed.
     */
    private final EnumSet<STEP> initialisationStatus = EnumSet.noneOf(STEP.class);

    /**
     * Instantiates a new AuthService object
     */
    public AuthService() {}

    /**
     * Throws an {@link IllegalStateException} if the required initialisation steps are not executed
     * in the right order
     */
    private void failIfNotReady() {
        if (initialisationStatus.containsAll(Arrays.asList(STEP.values()))) {
            return;
        }

        List<String> methodsToBeInvoked = new ArrayList<>(2);

        if (!initialisationStatus.contains(STEP.CONFIGURED)) {
            methodsToBeInvoked.add(STEP.CONFIGURED.methodName);
        }

        if (!initialisationStatus.contains(STEP.INITIALIZED)) {
            methodsToBeInvoked.add(STEP.INITIALIZED.methodName);
        }

        throw new IllegalStateException(String.format(
                        "The AuthService has not been correctly initialised. Following methods needs to be called: %s",
                        Arrays.toString(methodsToBeInvoked.toArray())));
    }

    /**
     * Return the user that is currently logged and is still valid. Otherwise returns null
     *
     * @return the current logged in. Could be null.
     */
    public UserPrincipal currentUser() {
        failIfNotReady();

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
     * Log in the user with the given authentication options. At the moment, only OIDC protocol is
     * supported. The login will be asynchronous.
     *
     * @param authOptions the authentication options
     * @param callback the callback function that will be invoked with the user info
     */
    public void login(@NonNull final DefaultAuthenticateOptions authOptions,
                    @NonNull final Callback<UserPrincipal> callback) {
        failIfNotReady();
        oidcAuthenticatorImpl.authenticate(authOptions, callback);
    }

    /**
     * Delete the the current tokens/authentication state.
     */
    public void deleteTokens() {
        failIfNotReady();
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
     * @param callback the callback function to be invoked
     */
    public void logout(@NonNull final UserPrincipal principal,
                    @NonNull final Callback<UserPrincipal> callback) {
        failIfNotReady();
        this.oidcAuthenticatorImpl.logout(principal, callback);
    }


    @Override
    public String type() {
        return "keycloak";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.mobileCore = nonNull(core, "mobileCore");
        this.serviceConfiguration = nonNull(serviceConfiguration, "serviceConfiguration");
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);

        initialisationStatus.add(STEP.CONFIGURED);
    }

    /**
     * Initialize the module. This should be called before any other method when using the module.
     *
     * @param context the current application context
     * @param authServiceConfiguration the configuration of the auth service
     */
    public void init(final Context context,
                    final AuthServiceConfiguration authServiceConfiguration) {
        if (!initialisationStatus.contains(STEP.CONFIGURED)) {
            throw new IllegalStateException(
                            "configure method must be called before the init method");
        }

        CertificatePinningCheck pinningCheck = new CertificatePinningCheck(this.mobileCore.getHttpLayer());
        pinningCheck.check(this.serviceConfiguration.getUrl());

        this.appContext = nonNull(context, "context");
        this.authStateManager = AuthStateManager.getInstance(context);
        this.authServiceConfiguration =
                        nonNull(authServiceConfiguration, "authServiceConfiguration");
        this.jwksManager = new JwksManager(this.appContext, this.mobileCore,
                        this.authServiceConfiguration);
        this.oidcAuthenticatorImpl = new OIDCAuthenticatorImpl(this.serviceConfiguration,
                        this.authServiceConfiguration, this.authStateManager,
                        new AuthorizationServiceFactory(appContext), jwksManager, pinningCheck);
        initialisationStatus.add(STEP.INITIALIZED);
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {

    }
}
