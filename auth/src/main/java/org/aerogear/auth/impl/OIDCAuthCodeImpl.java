package org.aerogear.auth.impl;

import org.aerogear.auth.AuthServiceConfig;
import org.aerogear.auth.credentials.ICredential;

import java.security.Principal;

/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthCodeImpl extends OIDCTokenAuthenticatorImpl {

    public OIDCAuthCodeImpl(final AuthServiceConfig config) {
        super(config);
    }

    /**
     * @param credential Ignored.
     * @return authenticated Principal
     */
    @Override
    public Principal authenticate(final ICredential credential) {
        throw new IllegalStateException("Not implemented");
    }
}
