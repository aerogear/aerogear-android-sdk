package org.aerogear.mobile.auth.user;

import org.aerogear.mobile.auth.user.RoleType;
import org.aerogear.mobile.auth.user.UserRole;
import org.aerogear.mobile.auth.user.UserPrincipalImpl;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UserPrincipalImplTest {
    private UserPrincipalImpl userPrincipalImpl;
    private Set<UserRole> roles =  new HashSet<>();

    @Before
    public void setUp(){
        ServiceConfiguration serviceConfig = ServiceConfiguration.newConfiguration().build();
        UserRole cRole = new UserRole("cRole", RoleType.CLIENT, "ID-123456");
        UserRole rRole = new UserRole("rRole", RoleType.REALM, null);
        roles.add(cRole);
        roles.add(rRole);
        userPrincipalImpl = UserPrincipalImpl.
            newUser()
            .withRoles(roles)
            .withUsername("test-user")
            .build();
    }

    @After
    public void tearDown(){
        userPrincipalImpl = null;
    }

    @Test
    public void testHasRealmRoleFails(){
        assertEquals(userPrincipalImpl.hasRealmRole("notRRole"), false);
    }

    @Test
    public void testHasRealmRoleSucceeds(){
        assertEquals(userPrincipalImpl.hasRealmRole("rRole"), true);
    }

    @Test
    public void testHasClientRoleFails(){
        assertEquals(userPrincipalImpl.hasClientRole("cRole", "notid"), false);
        assertEquals(userPrincipalImpl.hasClientRole("notCRole", "ID-123456"), false);
        assertEquals(userPrincipalImpl.hasClientRole("notCRole", "notid"), false);
    }

    @Test
    public void testHasClientRoleSucceeds(){
        assertEquals(userPrincipalImpl.hasClientRole("cRole", "ID-123456"), true);
    }

    @Test
    public void testGetRoles() {
        assertEquals(userPrincipalImpl.getRoles().size(), 2);
    }
}
