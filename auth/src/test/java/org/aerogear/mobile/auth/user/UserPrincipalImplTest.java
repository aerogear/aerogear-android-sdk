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
                    + "dReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI4OGQ2MDY0Ni00YzA4LTQwYmMtYTh"
                    + "jMy00MzA3MmI4N2ZmMGQiLCJleHAiOjE1MjA5NjA5OTAsIm5iZiI6MCwiaWF0IjoxNTIwOTU5MTkwLCJpc3MiOiJo"
                    + "dHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1Z"
                    + "CI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOi"
                    + "JJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MjA5NTkxODksInNlc3Npb25fc3RhdGUiOiJmZTR"
                    + "mN2YxZi0wMzJiLTRlMjEtOTZlMS0zMGFkMjA2NzJlNTEiLCJhY3IiOiIxIiwiYm9vbGVhbiI6dHJ1ZSwic3RyaW5n"
                    + "Ijoic3RyaW5nIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZ"
                    + "SI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmciLCJpbnQiOjEsIm"
                    + "xvbmciOjF9.OJ1K3h9kOccsLmAxNo_FOoy2L5BWTl2u9K3Y6HhteGKL8rd293sM856-Da8ZiuScSd6wGzk2lQjpCG"
                    + "Cv_YUaduRGtN7RMtI61P4zYeYZj4z08A65ZhgXUDqIMkCvqgcSFkBdJvKeZBGeogWttqu6k_0oMHIywgWrxFm9uNw"
                    + "66F8-3jwbOP-hdZDGFeCf9EhOcT9EzZ56nGRfWZI_FPjo0VRmRyixmLF3ulIZ_yrlcRAUNdW3g-GVTsUgO2DIXW45"
                    + "xt-_Kz1AQRhMRW50775_TZOlWt__wRrt9-Y4Qn_KHfiPaCqDAbzAdNpJJLo0S_yemEqV9pEWEQE4ZoVA9hwypQ";

    @Before
    public void setUp() {
        ServiceConfiguration serviceConfig = ServiceConfiguration.newConfiguration().build();
        UserRole cRole = new UserRole("cRole", RoleType.RESOURCE, "ID-123456");
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
    public void testHasResourceRoleFails() {
        assertEquals(userPrincipalImpl.hasResourceRole("cRole", "notid"), false);
        assertEquals(userPrincipalImpl.hasResourceRole("notCRole", "ID-123456"), false);
        assertEquals(userPrincipalImpl.hasResourceRole("notCRole", "notid"), false);
    }

    @Test
    public void testHasResourceRoleSucceeds() {
        assertEquals(userPrincipalImpl.hasResourceRole("cRole", "ID-123456"), true);
    }

    @Test
    public void testGetRoles() {
        assertEquals(userPrincipalImpl.getRoles().size(), 2);
    }

    @Test
    public void testGetRealmRoles() {
        assertEquals(userPrincipalImpl.getRealmRoles().size(), 1);
    }

    @Test
    public void testGetResourceRoles() {
        assertEquals(userPrincipalImpl.getResourceRoles().size(), 1);
    }

    @Test
    public void testGetCustomStringAttributes() {
        assertEquals("string", userPrincipalImpl.getCustomStringAttribute("string"));
        assertEquals(null,
                        userPrincipalImpl.getCustomStringAttribute("nonExistentCustomAttribute"));
    }

    @Test
    public void testGetCustomBooleanAttributes() {
        assertEquals(true, userPrincipalImpl.getCustomBooleanAttribute("boolean"));
        assertEquals(false,
                        userPrincipalImpl.getCustomBooleanAttribute("nonExistentCustomAttribute"));
    }

    @Test
    public void testGetCustomIntegerAttributes() {
        assertEquals(new Integer(1), userPrincipalImpl.getCustomIntegerAttribute("int"));
        assertEquals(new Integer(0),
                        userPrincipalImpl.getCustomIntegerAttribute("nonExistentCustomAttribute"));
    }

    @Test
    public void testGetCustomLongAttributes() {
        assertEquals(new Long(1L), userPrincipalImpl.getCustomLongAttribute("long"));
        assertEquals(new Long(0L),
                        userPrincipalImpl.getCustomLongAttribute("nonExistentCustomAttribute"));
    }
}
