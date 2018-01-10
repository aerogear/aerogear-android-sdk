package org.aerogear.mobile.keycloak_service_module;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.JsonReader;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class KeyCloakService implements ServiceModule {

    private final Context appContext;
    private KeyCloakConfig config;

    public KeyCloakService(@NonNull Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }


    /**
     * Loads keycloak.json from R.raw.keycloak
     *
     * @param resId
     * @throws RuntimeException if a IOException is thrown during bootstrap.
     */
    private void bootstrap(int resId) {
        try ( InputStream keycloakConfigStream = appContext.getResources().openRawResource(resId);
              JsonReader reader = new JsonReader(new InputStreamReader(keycloakConfigStream, "UTF-8") ) ) {
            config = KeyCloakConfig.parse(reader);
        } catch (IOException e) {
            //MobileCore.defaultLog().error(e.getMessage(), e);
        }
    }


    @Override
    public void bootstrap(Object... args) {
        if (args.length == 1 && args[0] instanceof Integer) {
            bootstrap((int)args[0]);
        } else {
            throw new IllegalArgumentException("KeyCloakService.bootstrap requires exactly one argument of type int.");
        }
    }
}
