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
 *
 * Usage.java
 * ```
 * MobileCore core = new MobileCore.Builder(context, R.raw.mobile_core).build();
 * core.feature(Keycloak.class).login();// would begin a OAuth flow.
 * core.feature(MyRestService.class).upload(myData);// Would upload myData and be secured by KeyCloak
 * ```
 */
public final class MobileCore {


    private final Context context;
    private final String mobileServiceFileName;
    private Map<String, ServiceConfiguration> configurationMap;

    private MobileCore(@NonNull Context context, String mobileServiceFileName) {
        this.context = context.getApplicationContext();
        this.mobileServiceFileName = mobileServiceFileName;
    }

    public Logger defaultLog() {
        return (String message, Exception e) -> {
            Log.e("MOBILE_CORE", message, e);
        };
    }

    public void bootstrap(Object... args) {

        try (InputStream configStream = context.getAssets().open(this.mobileServiceFileName);) {
            this.configurationMap = MobileCoreJsonParser.parse(configStream);
        } catch (JSONException | IOException e) {
            defaultLog().error(e.getMessage(), e);
            throw new BootstrapException(String.format("%s could not be loaded", mobileServiceFileName), e);
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
        private String mobileServiceFileName = "mobile-services.json";

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * The filename of the mobile service configuration file in the assets directory.
         *
         * defaults to mobile-services.json
         *
         * @return the current value, never null.
         */
        @NonNull
        public String getMobileServiceFileName() {
            return mobileServiceFileName;
        }

        /**
         * The filename of the mobile service configuration file in the assets directory.
         *
         * defaults to mobile-services.json
         *
         * @param mobileServiceFileName a new filename.  May not be null.
         * @return the current value, never null.
         */
        public Builder setMobileServiceFileName(@NonNull String mobileServiceFileName) {

            if (mobileServiceFileName == null) {
                throw new IllegalArgumentException("mobileServiceFileName may not be null");
            }

            this.mobileServiceFileName = mobileServiceFileName;
            return this;
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
                MobileCore core = new MobileCore(context, mobileServiceFileName);

                core.bootstrap();
                return core;
            } else {
                throw new IllegalStateException("MobileCore has already been built");
            }
        }
    }

}
