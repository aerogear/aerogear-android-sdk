package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Authenticates token credentials
 */
public class OIDCTokenAuthenticatorImpl extends AbstractAuthenticator {

    private static final Uri REDIRECT_URI = Uri.parse("com.feedhenry.securenativeandroidtemplate:/callback");  // unsure what this should be?
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

    public void logout(Principal principal) {
        // Get user's identity token
        OIDCCredentials credentials = (OIDCCredentials) ((IUserPrincipal) principal).getCredentials();
        String identityToken = credentials.getIdentityToken();

        // Construct the logout URL
        URL logoutUrl = parseLogoutURL(identityToken);
        String serverHostname = logoutUrl.getHost();

        // Build the OkHttpClient client
        OkHttpClient client = buildHttpClient(logoutUrl, serverHostname);

        // Construct and invoke logout request
        performLogout(client, logoutUrl);
    }

    private void performLogout(OkHttpClient client, URL logoutUrl) {
        HttpServiceModule serviceModule = new OkHttpServiceModule(client);

        HttpRequest request = serviceModule.newRequest();
        request.get(logoutUrl.toString());

        HttpResponse response = request.execute();
        response.onComplete(new LogoutCompleteHandler(response, AuthStateManager.getInstance())); // will compile when AuthStateManaer is refactored to singleton
        response.waitForCompletionAndClose();
    }

    private OkHttpClient buildHttpClient(URL logoutUrl, String serverHostname) {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) logoutUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();

        }

        SSLSocketFactory sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(serverHostname);
        X509TrustManager trustManager = TrustKit.getInstance().getTrustManager(serverHostname);
        connection.setSSLSocketFactory(sslSocketFactory);

        OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

        return client;
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
