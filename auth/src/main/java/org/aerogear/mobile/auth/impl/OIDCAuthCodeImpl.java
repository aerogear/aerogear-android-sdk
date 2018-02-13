package org.aerogear.mobile.auth.impl;

import org.aerogear.mobile.auth.AuthConfiguration;
import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.credentials.ICredential;
import org.aerogear.mobile.auth.utils.UserIdentityParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.json.JSONException;

import java.security.Principal;


/**
 * Authenticates the user by using OpenID Connect.
 */
public class OIDCAuthCodeImpl extends OIDCTokenAuthenticatorImpl {

    /**
     * Creates a new OIDCAuthCodeImpl object
     *
     * @param serviceConfig {@link ServiceConfiguration}
     * @param authConfiguration {@link AuthConfiguration}
     */
    public OIDCAuthCodeImpl(final ServiceConfiguration serviceConfig, final AuthConfiguration authConfiguration) {
        super(serviceConfig, authConfiguration);
    }

    /**
     * Builds a new OIDCUserPrincipalImpl object after the user's credential has been authenticated
     *
     * @param credential the OIDC credential for the user
     * @return a new OIDCUserPrincipalImpl object with the user's identity {@link UserIdentityParser} that was decoded from the user's credential.
     * @throws AuthenticationException
     * @see OIDCTokenAuthenticatorImpl#authenticate(ICredential)
     */
    @Override
    public Principal authenticate(final ICredential credential) throws AuthenticationException {
        Principal user;
        try {
            UserIdentityParser parser = new UserIdentityParser(credential, this.getServiceConfig());
            user = OIDCUserPrincipalImpl
                .newUser()
                .withAuthenticator(this)
                .withUsername(parser.parseUsername())
                .withCredentials(credential)
                .withEmail(parser.parseEmail())
                .withRoles(parser.parseRoles())
                .build();
        } catch (JSONException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
    return user;
    }
}
