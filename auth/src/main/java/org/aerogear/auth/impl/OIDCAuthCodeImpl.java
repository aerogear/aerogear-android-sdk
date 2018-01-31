package org.aerogear.auth.impl;

import org.aerogear.auth.credentials.ICredential;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.security.Principal;

/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthCodeImpl extends OIDCTokenAuthenticatorImpl {

    public OIDCAuthCodeImpl(final ServiceConfiguration serviceConfig) {
        super(serviceConfig);
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
