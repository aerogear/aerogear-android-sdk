package org.aerogear.auth.impl;


import android.content.Context;

import org.aerogear.auth.IRole;
import org.aerogear.auth.IUserPrincipal;
import org.aerogear.auth.credentials.ICredential;
import org.aerogear.auth.credentials.OIDCCredentials;
import org.aerogear.auth.utils.AuthStateManager;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.security.Principal;
import java.util.Collection;

import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OIDCTokenAuthenticatorImplTest {

    @Mock
    private ServiceConfiguration config;
    @Mock
    private OIDCCredentials credentials;
    @Mock
    private Context context;

    private Principal principal = new IUserPrincipal() {
        @Override
        public boolean hasRole(IRole role) {
            return false;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Collection<IRole> getRoles() {
            return null;
        }

        @Override
        public ICredential getCredentials() {
            return credentials;
        }
    };

    private OIDCTokenAuthenticatorImpl tokenAuthenticator;

    private final String IDENTITY_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIzMmJjMjc4Yy00MmUyLTQwMjctOWVkZi1mZTM3OTcxYjkyZTQiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTY2MzI2NDcsInNlc3Npb25fc3RhdGUiOiJjMjU1ZjBhYy04MDkxLTRjOTItOGYzYS00OGZmYjg4MWE3MGIiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.h_WzsxdBUG0eBMz_zgEyl0yOMlVJ1YkNTRaiWje7Do3s90URP3y_nFkz9NNMSfoADPLZdTWe9fWL1h2LWfc6WUmasr4hx9zks76Asz78rZLyC4ARuGSwUncCiLxdB4chq-SAOoXYiZV6K0St-UhBkV90jd6xc5WIKrTItqDenuMjkc5ePU7Sx9MYE2fS4dKGNdRGff8uAXELpM66eVxM6xn-xBTEuHPQvR-vFoFLUssxMVk8IjsAb2H9epioWshQkY0vL70z58egAqiLVluVFecRXmsrLGrVnRx-S3UjQRKLyo_V3CyFCfGKspGtsXZYhmzzmMTdqPU_wLax59A69w";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuthStateManager.getInstance(context);
        tokenAuthenticator = new OIDCTokenAuthenticatorImpl(config);
    }

    @Test
    public void testLogout() {
        when(credentials.getIdentityToken()).thenReturn(IDENTITY_TOKEN);

        when(config.getProperty("auth-server-url")).thenReturn("http://keycloak-myproject.192.168.37.1.nip.io/auth");
        when(config.getProperty("realm")).thenReturn("myproject");


        tokenAuthenticator.logout(principal);

//        AuthStateManager authStateManager = AuthStateManager.getInstance();
//        verify(authStateManager, times(1)).save(null);
    }
}
