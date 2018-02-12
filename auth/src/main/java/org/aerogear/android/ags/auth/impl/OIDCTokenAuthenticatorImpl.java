package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthConfiguration;
import org.aerogear.android.ags.auth.AuthStateManager;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.IUserPrincipal;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.android.ags.auth.utils.LogoutCompleteHandler;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import static org.aerogear.android.ags.auth.AuthStateManager.getInstance;

/**
 * Authenticates token credentials
 */
public class OIDCTokenAuthenticatorImpl extends AbstractAuthenticator {

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private final AuthConfiguration authConfiguration;

    public OIDCTokenAuthenticatorImpl(final ServiceConfiguration config, AuthConfiguration authConfiguration) {
        super(config);
        this.authConfiguration = authConfiguration;
    }

    @Override
    public Principal authenticate(ICredential credential) throws AuthenticationException {
        if (credential instanceof OIDCCredentials) {
            // Authenticate the credential
            throw new IllegalStateException("Not implemented");
        }
        // This authenticator can't manage this type of credential
        throw new IllegalArgumentException("Invalid Credential");

    }

    /**
     * Logs out a user from openID connect server.
     *
     * @param principal principal to be logged out.
     */
    public void logout(final Principal principal) {
        // Get user's identity token
        OIDCCredentials credentials = (OIDCCredentials) ((IUserPrincipal) principal).getCredentials();
        if (credentials != null) {
            String identityToken = credentials.getIdentityToken();

            // Construct the logout URL
            URL logoutUrl = parseLogoutURL(identityToken);

            // Construct and invoke logout request
            performLogout(logoutUrl);
        } else {
            throw new IllegalStateException("User's credentials cannot be null");
        }
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
        HttpResponse response = request.execute();
        AuthStateManager authStateManager = getInstance();
        response.onComplete(new LogoutCompleteHandler(response, authStateManager));
        response.waitForCompletionAndClose();
    }

    /**
     * Constructs the logout url using the user's idenity token.
     *
     * @param identityToken {@link OIDCCredentials#getIdentityToken()}
     * @return the formatted logout url.
     */
    private URL parseLogoutURL(final String identityToken) {
        String serverUrl = this.getServiceConfig().getProperty("auth-server-url");
        String realmId = this.getServiceConfig().getProperty("realm");
        String baseUrl = String.format("%s/auth/realms/%s/protocol/openid-connect", serverUrl, realmId);
        String redirectUri =  authConfiguration.getRedirectUri().toString();

        String logoutRequestUri = String.format("%s/logout?%s=%s&%s=%s", baseUrl, TOKEN_HINT_FRAGMENT, identityToken, REDIRECT_FRAGMENT, redirectUri.toString());

        URL url = null;
        try {
            url = new URL(logoutRequestUri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not parse logout url");
        }
        return url;
    }
}
