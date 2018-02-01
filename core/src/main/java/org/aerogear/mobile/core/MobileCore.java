package org.aerogear.mobile.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.BootstrapException;
import org.aerogear.mobile.core.exception.NotInitializedException;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    private static MobileCore instance;

    private final String configFileName;
    private final HttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> servicesConfig;

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     */
    public static void init(Context context) throws BootstrapException {
        init(context, new Options());
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @param options AeroGear initialization options
     */
    public static void init(Context context, Options options) throws BootstrapException {
        if (instance == null) {
            instance = new MobileCore(context, options);
        }
    }

    @VisibleForTesting()
    public static void cleanup() {
        instance = null;
    }

    /**
     * Creates a MobileCore instance
     *
     * @param context Application context
     */
    private MobileCore(Context context, Options options) throws BootstrapException {
        this.configFileName = options.configFileName;

        // -- Parse JSON config file
        try (InputStream configStream = context.getAssets().open(configFileName)) {
            this.servicesConfig = MobileCoreJsonParser.parse(configStream);
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new BootstrapException(message, exception);
        }

        // -- Setting default http layer

        if (options.getHttpServiceModule() == null) {
            ServiceConfiguration configuration = this.servicesConfig.get("http");
            if (configuration == null) {
                configuration = new ServiceConfiguration.Builder().build();
            }
            this.httpLayer = new OkHttpServiceModule(configuration);
        } else {
            this.httpLayer = options.getHttpServiceModule();
        }
    }

    /**
     * Check if the init was called
     *
     * @throws NotInitializedException
     */
    private static void checkIfInitialized() throws NotInitializedException {
        if (instance == null) {
            throw new NotInitializedException();
        }
    }

    /**
     * Returng the configuration for this service from the JSON config file
     *
     * @param type Service type/name
     * @return the configuration for this service from the JSON config file
     */
    public static ServiceConfiguration getServiceConfiguration(String type)
        throws NotInitializedException {
        checkIfInitialized();
        return instance.servicesConfig.get(type);
    }


    public static HttpServiceModule getHttpLayer() {
        checkIfInitialized();
        return instance.httpLayer;
    }

    public static final class Options {

        private String configFileName = "mobile-services.json";
        // Don't have a default implementation because it should use configuration
        private HttpServiceModule httpServiceModule;

        public Options() {
        }

        public Options(String configFileName, HttpServiceModule httpServiceModule) {
            this.configFileName = configFileName;
            this.httpServiceModule = httpServiceModule;
        }

        public String getConfigFileName() {
            return configFileName;
        }

        public Options setConfigFileName(@NonNull String configFileName) {
            this.configFileName = configFileName;
            return this;
        }

        public HttpServiceModule getHttpServiceModule() {
            return httpServiceModule;
        }

        public Options setHttpServiceModule(@NonNull HttpServiceModule httpServiceModule) {
            this.httpServiceModule = httpServiceModule;
            return this;
        }

    }

}
