package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import static org.aerogear.auth.utils.AuthStateManager.getInstance;

/**
 * Authenticates token credentials
 */
public class OIDCTokenAuthenticatorImpl extends AbstractAuthenticator {

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private final AuthService authService = AuthService.getInstance();
    private final Uri redirectUri = authService.getAuthConfiguration().getRedirectUri();
    private final AuthStateManager authStateManager = getInstance();

    public OIDCTokenAuthenticatorImpl(final ServiceConfiguration config) {
        super(config);
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

    protected void performLogout(URL logoutUrl) {
        // Using the default OkHttpServiceModule for now. This will need to be refactored for cert pinning stuff
        HttpServiceModule serviceModule = new OkHttpServiceModule();

        HttpRequest request = serviceModule.newRequest();
        request.get(logoutUrl.toString());

        HttpResponse response = request.execute();
        response.onComplete(new LogoutCompleteHandler(response, authStateManager));
        response.waitForCompletionAndClose();
    }

    private URL parseLogoutURL(String identityToken) {
        String serverUrl = this.getServiceConfig().getProperty("auth-server-url");
        String realmId = this.getServiceConfig().getProperty("realm");
        String baseUrl = String.format("%s/auth/realms/%s/protocol/openid-connect", serverUrl, realmId);

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
