package org.aerogear.mobile.auth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.aerogear.mobile.auth.authenticator.AuthorizationServiceFactory;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

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
     * Variable holdind current status of the service. Used to check if the service is ready to be
     * used.
     */
    private final ReadynessStatus status = new ReadynessStatus();

    /**
     * This class is used to hold the status of the service and, if required, throw and exception
     * with appropriate message describing why the service is not ready yet.
     */
    private static class ReadynessStatus {
        /**
         * Enumeration of all the steps that must be executed to make this service ready
         */
        private enum STEP {
            /**
             * This steps is related to the 'configure' method
             */
            CONFIGURED(1<<0, "configure"),
            /**
             * This step is related to the 'initialize' method
             */
            INITIALIZED(1<<1, "initialize");

            /**
             * We store here all the executed step. Each bit represent a different step.
             */
            byte bitValue;

            /**
             * The mothod that must be invoked to perform the required step.
             */
            String methodName;

            STEP(int val, String methodName) {
                this.bitValue = (byte)val;
                this.methodName = methodName;
            }
        }

        /**
         * Current status. When started, all steps must be executed.
         */
        private byte initialisationStatus = 0;

        /**
         * Mark one step as executed.
         * @param step executed step
         */
        public void updateStatus(STEP step) {
            initialisationStatus |= step.bitValue;
        }

        /**
         * Checks if the provided step has been executed.
         * @param step step to be checked
         * @return true if it has been executed
         */
        public boolean checkState(STEP step) {
            return ((initialisationStatus & step.bitValue) > 0);
        }

        /**
         * Invoked to check if the service is ready. If it is not ready, an IllegalStateException is
         * thrown with an appropriate error message.
         */
        public void checkReadyness() {

            if (initialisationStatus == (STEP.CONFIGURED.bitValue | STEP.INITIALIZED.bitValue)) {
                // The service is ready
                return;
            }

            List<String> methodsToBeInvoked = new ArrayList<>(2);

            if ((initialisationStatus & STEP.CONFIGURED.bitValue) == 0) {
                methodsToBeInvoked.add(STEP.CONFIGURED.methodName);
            }

            if ((initialisationStatus & STEP.INITIALIZED.bitValue) == 0) {
                methodsToBeInvoked.add(STEP.INITIALIZED.methodName);
            }

            throw new IllegalStateException(
                String.format("The AuthService has not been correctly initialised. Following methods needs to be called: %s",
                    Arrays.toString(methodsToBeInvoked.toArray())));
        }
    }

    /**
     * Instantiates a new AuthService object
     */
    public AuthService() {}

    /**
     * Return the user that is currently logged and is still valid. Otherwise returns null
     * @return the current logged in. Could be null.
     */
    public UserPrincipal currentUser() {
        status.checkReadyness();

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
    public void login(@NonNull final OIDCAuthenticateOptions authOptions, @NonNull final Callback<UserPrincipal> callback) {
        status.checkReadyness();
        oidcAuthenticatorImpl.authenticate(authOptions, callback);
    }

    /**
     * This function should be called in the start activity's "onActivityResult" method to allow the SDK to process the response from the authentication server.
     * @param data The intent data that is passed to "onActivityResult"
     */
    public void handleAuthResult(@NonNull final Intent data) {
        oidcAuthenticatorImpl.handleAuthResult(data);
    }

    /**
     * Log out the given principal.
     * The logout will be asynchronous.
     *
     * @param principal principal to be logged out
     */
    public void logout(@NonNull final UserPrincipal principal) {
        status.checkReadyness();
        this.oidcAuthenticatorImpl.logout(principal);
    }


    @Override
    public String type() {
        return "keycloak";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.logger = MobileCore.getLogger();
        this.serviceConfiguration = nonNull(serviceConfiguration, "serviceConfiguration");
        this.keycloakConfiguration = new KeycloakConfiguration(serviceConfiguration);

        status.updateStatus(ReadynessStatus.STEP.CONFIGURED);
    }

    /**
     * Initialize the module. This should be called before any other method when using the module.
     * @param context
     */
    public void init(final Context context, final AuthServiceConfiguration authServiceConfiguration) {
        if (!status.checkState(ReadynessStatus.STEP.CONFIGURED)) {
            throw new IllegalStateException("configure method must be called before the init method");
        }

        this.appContext = nonNull(context, "context");
        this.authStateManager = AuthStateManager.getInstance(context);
        this.authServiceConfiguration = nonNull(authServiceConfiguration, "authServiceConfiguration");
        this.oidcAuthenticatorImpl = new OIDCAuthenticatorImpl(this.serviceConfiguration, this.authServiceConfiguration, this.authStateManager, new AuthorizationServiceFactory(appContext));

        status.updateStatus(ReadynessStatus.STEP.INITIALIZED);
    }

    @Override
    public boolean requiresConfiguration() { return true; }

    @Override
    public void destroy() {

    }
}
