package org.aerogear.mobile.auth.user;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

@RunWith(RobolectricTestRunner.class)
public class UserPrincipalImplTest {

    private UserPrincipalImpl userPrincipalImpl;
    private Set<UserRole> roles = new HashSet<>();
    private String identityToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQW"
                    + "dReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI0MzgzOTc2NC0yOGY2LTQwOTAtYTI"
                    + "0ZS04OTM1ODUwMGE3ZjYiLCJleHAiOjE1MjA5MzY3NDIsIm5iZiI6MCwiaWF0IjoxNTIwOTM0OTQyLCJpc3MiOiJo"
                    + "dHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1Z"
                    + "CI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOi"
                    + "JJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MjA5MzQ5NDIsInNlc3Npb25fc3RhdGUiOiJlZmE"
                    + "5YjcyOC1iZDg2LTQ0OWUtYjVjZi1hNzdlNDU2MGE5ODYiLCJhY3IiOiIxIiwiY3VzdG9tQXR0cmlidXRlIjp0cnVl"
                    + "LCJuYW1lIjoiVXNlciAxIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjEiLCJnaXZlbl9uYW1lIjoiVXNlciIsI"
                    + "mZhbWlseV9uYW1lIjoiMSIsImVtYWlsIjoidXNlcjFAZmVlZGhlbnJ5Lm9yZyJ9.L-tUj70hKX-NY9mbK6wSuDlLl"
                    + "t9xzARFmZbgoOZg7T_7sdriGPbWpQ8anlZBGgOuwbd-nxHPlaCOBWX24SnHiXl27uy_1mWJWDUSpJr2UG_W8DUkH_"
                    + "ecgJ7Y5DbnBMXQE2zNET9TdAcnWAs5yXyr0ghzJ1uIpMNkdxURVzjOWhbcnEdwfMD7lGOGQDqXJdAW2WEQ4edDcOI"
                    + "OKgQ7ODK979FFh5IZlR7QRneNt4MaRkEztPcltlMWMC456FKsqsT8PIHVkRbmB3IGaAY17iN2MekSuAE-_klFtyZg"
                    + "cOVb9GOWOs0uxGUHB_QQZ6D1wFX8DzXALLhLYX1vPbVKcMzSew";

    @Before
    public void setUp() {
        ServiceConfiguration serviceConfig = ServiceConfiguration.newConfiguration().build();
        UserRole cRole = new UserRole("cRole", RoleType.CLIENT, "ID-123456");
        UserRole rRole = new UserRole("rRole", RoleType.REALM, null);
        roles.add(cRole);
        roles.add(rRole);
        userPrincipalImpl = UserPrincipalImpl.newUser().withRoles(roles).withUsername("test-user")
                        .withIdentityToken(identityToken).build();
    }

    @After
    public void tearDown() {
        userPrincipalImpl = null;
    }

    @Test
    public void testHasRealmRoleFails() {
        assertEquals(userPrincipalImpl.hasRealmRole("notRRole"), false);
    }

    @Test
    public void testHasRealmRoleSucceeds() {
        assertEquals(userPrincipalImpl.hasRealmRole("rRole"), true);
    }

    @Test
    public void testToStringImpl() {
        Assert.assertTrue(userPrincipalImpl.toString().contains("cRole"));
        Assert.assertTrue(userPrincipalImpl.toString().contains("rRole"));
    }

    @Test
    public void testHasClientRoleFails() {
        assertEquals(userPrincipalImpl.hasClientRole("cRole", "notid"), false);
        assertEquals(userPrincipalImpl.hasClientRole("notCRole", "ID-123456"), false);
        assertEquals(userPrincipalImpl.hasClientRole("notCRole", "notid"), false);
    }

    @Test
    public void testHasClientRoleSucceeds() {
        assertEquals(userPrincipalImpl.hasClientRole("cRole", "ID-123456"), true);
    }

    @Test
    public void testGetRoles() {
        assertEquals(userPrincipalImpl.getRoles().size(), 2);
    }

    @Test
    public void testGetCustomAttribute() {
        assertEquals("true", userPrincipalImpl.getCustomAttribute("customAttribute"));
        assertEquals(null, userPrincipalImpl.getCustomAttribute("nonExistentCustomAttribute"));
    }
}
