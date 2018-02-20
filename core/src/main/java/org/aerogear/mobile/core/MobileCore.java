package org.aerogear.mobile.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.aerogear.android.core.BuildConfig;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.metrics.MetricsPublisher;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    private static final String TAG = "AEROGEAR/CORE";
    private static Logger logger = new LoggerAdapter();
    private static String appVersion;


    private final Context context;
    private final String configFileName;
    private final HttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> servicesConfig;
    private final Map<Class<? extends ServiceModule>, ServiceModule> services = new HashMap<>();

    /**
     * Creates a MobileCore instance
     *
     * @param context Application context
     */
    private MobileCore(Context context, Options options)
        throws InitializationException, IllegalStateException {
        if (context == null) {
            throw new IllegalStateException("Context should not be null");
        }

        this.context = context.getApplicationContext();
        this.configFileName = options.configFileName;

        // -- Allow to override the default logger
        if (options.logger != null) {
            logger = options.logger;
        }

        // -- Parse JSON config file
        try (InputStream configStream = context.getAssets().open(configFileName)) {
            this.servicesConfig = MobileCoreJsonParser.parse(configStream);
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new InitializationException(message, exception);
        }

        // -- Set the app version variable
        appVersion = getAppVersion(context);

        // -- Setting default http layer
        if (options.httpServiceModule == null) {
            OkHttpServiceModule httpServiceModule = new OkHttpServiceModule();

            ServiceConfiguration configuration = this.servicesConfig.get(httpServiceModule.type());
            if (configuration == null) {
                configuration = new ServiceConfiguration.Builder().build();
            }

            httpServiceModule.configure(this, configuration);

            this.httpLayer = httpServiceModule;
        } else {
            this.httpLayer = options.httpServiceModule;
        }

        sendDefaultMetrics();
    }

    private void sendDefaultMetrics() {
        try {
            MetricsService metrics = getInstance(MetricsService.class);
            metrics.sendDefaultMetrics();
        } catch (ConfigurationNotFoundException e) {
            logger.debug(TAG, "Metrics not configured, not sending anything");
        }
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @return MobileCore instance
     */
    public static MobileCore init(Context context) throws InitializationException {
        return init(context, new Options());
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @param options AeroGear initialization options
     * @return MobileCore instance
     */
    public static MobileCore init(Context context, Options options) throws InitializationException {
        return new MobileCore(context, options);
    }

    /**
     * Called when mobile core instance need to be destroyed
     */
    public void destroy() {
        for (Class<? extends ServiceModule> serviceKey : services.keySet()) {
            ServiceModule serviceModule = services.get(serviceKey);
            serviceModule.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getInstance(Class<T> serviceClass) {
        return (T) getInstance(serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getInstance(Class<T> serviceClass,
                                                   ServiceConfiguration serviceConfiguration)
        throws InitializationException {

        if (services.containsKey(serviceClass)) {
            return (T) services.get(serviceClass);
        }

        try {
            ServiceModule serviceModule = serviceClass.newInstance();

            if (serviceConfiguration == null) {
                serviceConfiguration = getServiceConfiguration(serviceModule.type());
            }

            serviceModule.configure(this, serviceConfiguration);

            services.put(serviceClass, serviceModule);

            return (T) serviceModule;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    /**
     * Get application context
     * @return Application context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returng the configuration for this service from the JSON config file
     *
     * @param type Service type/name
     * @return the configuration for this service from the JSON config file
     */
    private ServiceConfiguration getServiceConfiguration(String type) {
        ServiceConfiguration serviceConfiguration = this.servicesConfig.get(type);
        if (serviceConfiguration == null) {
            throw new ConfigurationNotFoundException(type + " not found on " + this.configFileName);
        }
        return serviceConfiguration;
    }

    /**
     * Get the user app version from the package manager
     *
     * @param context Android application context
     * @return String app version name
     */
    private String getAppVersion(final Context context) throws InitializationException {
        try {
            return context
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Wrap in Initialization exception
            throw new InitializationException("Failed to read app version", e);
        }
    }

    public HttpServiceModule getHttpLayer() {
        return this.httpLayer;
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Get the version name of the SDK itself
     *
     * @return String SDK version
     */
    public static String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * Get the version of the user app
     *
     * @return String App version name
     */
    public static String getAppVersion() {
        return appVersion;
    }

    public static final class Options {

        private String configFileName = "mobile-services.json";
        // Don't have a default implementation because it should use configuration
        private HttpServiceModule httpServiceModule;
        private Logger logger = new LoggerAdapter();

        public Options() {
        }

        public Options(String configFileName, HttpServiceModule httpServiceModule) {
            this.configFileName = configFileName;
            this.httpServiceModule = httpServiceModule;
        }

        public Options setConfigFileName(@NonNull String configFileName) {
            this.configFileName = configFileName;
            return this;
        }

        public Options setHttpServiceModule(@NonNull HttpServiceModule httpServiceModule) {
            this.httpServiceModule = httpServiceModule;
            return this;
        }

        public Options setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }
    }
}
