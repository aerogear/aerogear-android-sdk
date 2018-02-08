package org.aerogear.android.ags.auth.impl;

import org.aerogear.android.ags.auth.AbstractAuthenticator;
import org.aerogear.android.ags.auth.AuthenticationException;
import org.aerogear.android.ags.auth.credentials.ICredential;
import org.aerogear.android.ags.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.security.Principal;

/**
 * Authenticates token credentials
 */
public class OIDCTokenAuthenticatorImpl extends AbstractAuthenticator {
    public OIDCTokenAuthenticatorImpl(final ServiceConfiguration serviceConfig) {
        super(serviceConfig);
    }

    @Override
    public Principal authenticate(ICredential credential) throws AuthenticationException {
        if (credential instanceof OIDCCredentials) {
            // Authenticate the credential
            throw new IllegalStateException("Not implemented");
        }

        // This authenticator can't manage this type of credential
        return null;
    }
}
