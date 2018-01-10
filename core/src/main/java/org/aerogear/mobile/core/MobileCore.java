package org.aerogear.mobile.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.logging.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * MobileCore is the entry point into AeroGear mobile services that are managed by the mobile-core
 * ¿feature( TODO: Get correct noun )? in OpenShift.
 * <p>
 * Usage.java
 * ```
 * MobileCore core = new MobileCore.Builder(context, R.raw.mobile_core).build();
 * core.feature(Keycloak.class).login();// would begin a OAuth flow.
 * core.feature(MyRestService.class).upload(myData);// Would upload myData and be secured by KeyCloak
 * ```
 */
public final class MobileCore implements ServiceModule {


    private final Context context;

    private Map<String, ServiceConfiguration> configurationMap;

    private MobileCore(@NonNull Context conext) {
        this.context = conext.getApplicationContext();
    }

    public Logger defaultLog() {
        return (String message, Exception e) -> {
            Log.e("MOBILE_CORE", message, e);
        };
    }

    @Override
    public void bootstrap(Object... args) {


        try (InputStream configStream = context.getAssets().open("mobile-core.json");) {
            this.configurationMap = MobileCoreJsonParser.parse(configStream);
        } catch (JSONException | IOException e) {
            defaultLog().error(e.getMessage(), e);
            throw new BootstrapException("mobile-core.json could not be loaded", e);
        }


        //startup known modules
    }

    public ServiceConfiguration getConfig(String configurationName) {
        return configurationMap.get(configurationName);
    }

    /**
     * Builder for MobileCore.  This class ensures that required properties are set and then creates
     * a MobileCore instance.
     * <p>
     * Usage.java
     * ```
     * MobileCore core = new MobileCore.Builder(context, R.raw.mobile_core).build();
     * ```
     */
    public static class Builder {

        private final Context context;
        private boolean built = false;

        public Builder(@NonNull Context context) {
            this.context = context;
        }


        /**
         * Builds a mobile core instance.  Please note that once this is built the Builder may not
         * be used again.
         *
         * @return a mobile core instance based on Builder parameters
         * @throws IllegalStateException if this builder has already been built.
         */
        public MobileCore build() {
            if (!built) {
                built = true;
                MobileCore core = new MobileCore(context);
                core.bootstrap();
                return core;
            } else {
                throw new IllegalStateException("MobileCore has already been built");
            }
        }
    }

}
