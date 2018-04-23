package org.aerogear.mobile.auth.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.RoleType;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserRole;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

@RunWith(RobolectricTestRunner.class)
public class UserIdentityParserTest {

    private UserIdentityParser parser;

    private KeycloakConfiguration keycloakConfiguration;

    private String accessToken =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw";

    private OIDCCredentials credential;

    @Before
    public void setup() throws AuthenticationException {
        ServiceConfiguration serviceConfig = ServiceConfiguration.newConfiguration()
                        .addProperty("resource", "client-app")
                        .addProperty("auth-server-url", "test.server.url")
                        .addProperty("realm", "test-realm").build();
        keycloakConfiguration = new KeycloakConfiguration(serviceConfig);
        credential = new OIDCCredentials() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
        parser = new UserIdentityParser(credential, keycloakConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUserIdentityParser_NullServiceConfig() throws AuthenticationException {
        parser = new UserIdentityParser(credential, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParsers_NullCredentials() throws AuthenticationException {
        parser = new UserIdentityParser(null, keycloakConfiguration);
    }

    @Test
    public void testParsers_WithCredentials() {
        String expectedUsername = "user1";
        String expectedEmail = "user1@feedhenry.org";
        UserRole expectedRealmRole = new UserRole("mobile-user", RoleType.REALM, null);
        UserRole expectedResourceRole = new UserRole("ios-access", RoleType.RESOURCE, "client-app");

        UserPrincipal user = parser.parseUser();
        String actualUsername = user.getUsername();
        String actualEmail = user.getEmail();
        Set<UserRole> actualRoles = user.getRoles();
        Set<UserRole> realmRoles = user.getRealmRoles();
        Set<UserRole> resourceRoles = user.getResourceRoles();

        assertEquals(expectedUsername, actualUsername);
        assertEquals(expectedEmail, actualEmail);
        assertEquals(1, realmRoles.size());
        assertEquals(1, resourceRoles.size());
        assertTrue(actualRoles.contains(expectedRealmRole));
        assertTrue(actualRoles.contains(expectedResourceRole));
    }

}
