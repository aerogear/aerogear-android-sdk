package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import okhttp3.OkHttpClient;

import static org.aerogear.auth.utils.AuthStateManager.*;

/**
 * Authenticates token credentials
 */
public class OIDCTokenAuthenticatorImpl extends AbstractAuthenticator {

    // This depends on how the developers app config will be passed into the SDK.
    // TODO: Once the above is implemented, this constant will need to be refactored to use the redirect uri chosen by the developer (they may have multiple redirect uri's defined)
    private static final Uri REDIRECT_URI = Uri.parse("com.feedhenry.securenativeandroidtemplate:/callback");

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

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
        String identityToken = credentials.getIdentityToken();

        // Construct the logout URL
        URL logoutUrl = parseLogoutURL(identityToken);

        // Using the default OkHttpServiceModule for now. This will need to be refactored for cert pinning stuff
        OkHttpClient client = new OkHttpClient();

        // Construct and invoke logout request
        performLogout(client, logoutUrl);
    }

    private void performLogout(OkHttpClient client, URL logoutUrl) {
        HttpServiceModule serviceModule = new OkHttpServiceModule(client);

        HttpRequest request = serviceModule.newRequest();
        request.get(logoutUrl.toString());

        HttpResponse response = request.execute();
        response.onComplete(new LogoutCompleteHandler(response, getInstance()));
        response.waitForCompletionAndClose();
    }

    private URL parseLogoutURL(String identityToken) {
        String serverUrl = this.getServiceConfig().getProperty("auth-server-url");
        String realmId = this.getServiceConfig().getProperty("realm");
        String baseUrl = String.format("%s/auth/realms/%s/protocol/openid-connect", serverUrl, realmId);

        String logoutRequestUri = String.format("%s/logout?%s=%s&%s=%s", baseUrl, TOKEN_HINT_FRAGMENT, identityToken, REDIRECT_FRAGMENT, REDIRECT_URI.toString());

        URL url = null;
        try {
            url = new URL(logoutRequestUri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
