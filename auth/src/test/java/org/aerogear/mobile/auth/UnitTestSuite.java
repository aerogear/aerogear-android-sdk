package org.aerogear.mobile.auth;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.auth.authenticator.OIDCAuthenticateOptionsTest;
import org.aerogear.mobile.auth.authenticator.OIDCAuthenticatorImplTest;
import org.aerogear.mobile.auth.configuration.AuthBrowserTest;
import org.aerogear.mobile.auth.configuration.AuthBrowserVersionRangeTest;
import org.aerogear.mobile.auth.configuration.AuthBrowsersTest;
import org.aerogear.mobile.auth.configuration.BrowserConfigurationTest;
import org.aerogear.mobile.auth.credentials.JwksManagerTest;
import org.aerogear.mobile.auth.credentials.OIDCCredentialsTest;
import org.aerogear.mobile.auth.user.UserPrincipalImplTest;
import org.aerogear.mobile.auth.utils.UserIdentityParserTest;

/**
 * Suite containing all unit tests in auth
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({OIDCAuthenticateOptionsTest.class, OIDCAuthenticatorImplTest.class,
                JwksManagerTest.class, OIDCCredentialsTest.class, UserPrincipalImplTest.class,
                UserIdentityParserTest.class, AuthStateManagerTest.class, AuthBrowsersTest.class,
                AuthBrowserVersionRangeTest.class, AuthBrowserTest.class,
                BrowserConfigurationTest.class})
public class UnitTestSuite {

}
