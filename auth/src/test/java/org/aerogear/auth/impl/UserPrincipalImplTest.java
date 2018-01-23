package org.aerogear.auth.impl;

import org.aerogear.auth.AbstractAuthenticator;
import org.aerogear.auth.AuthServiceConfig;
import org.aerogear.auth.ClientRole;
import org.aerogear.auth.IRole;
import org.aerogear.auth.RealmRole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserPrincipalImplTest {
    private UserPrincipalImpl userPrincipalImpl;

    @Before
    public void setUp(){
        AuthServiceConfig authServiceConfig = new AuthServiceConfig();
        AbstractAuthenticator abstractAuthenticator = new AbstractAuthenticator(authServiceConfig);
        ClientRole cRole = new ClientRole("cRole", "ID-123456");
        RealmRole rRole = new RealmRole("rRole");
        IRole[] roles = {cRole, rRole};
        userPrincipalImpl = UserPrincipalImpl.newUser().withRoles(roles).withAuthenticator(abstractAuthenticator).build();
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
