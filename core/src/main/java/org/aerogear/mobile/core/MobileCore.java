package org.aerogear.mobile.core;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.aerogear.android.core.BuildConfig;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;

import okhttp3.OkHttpClient;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    public static final String DEFAULT_CONFIG_FILE_NAME = "mobile-services.json";

    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_WRITE_TIMEOUT = 10;

    @SuppressLint("StaticFieldLeak")
    private static MobileCore instance;

    private final Context context;
    private final String appVersion;
    private final String configFileName;
    private final Logger logger;
    private final HttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> servicesConfig;
    private final Map<Class<? extends ServiceModule>, ServiceModule> services = new HashMap<>();

    /**
     * Creates a MobileCore instance
     *
     * @param context Application context
     */
    private MobileCore(final Context context, final Options options)
                    throws InitializationException, IllegalStateException {
        this.context = nonNull(context, "context").getApplicationContext();

        this.appVersion = getAppVersion(context);
        this.configFileName = options.configFileName;
        this.logger = options.logger;
        this.httpLayer = options.httpServiceModule;

        // -- Parse JSON config file
        try (final InputStream configStream = context.getAssets().open(configFileName)) {
            this.servicesConfig = MobileCoreJsonParser.parse(configStream);
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new InitializationException(message, exception);
        }
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @return MobileCore instance
     */
    public static void init(final Context context) throws InitializationException {
        init(context, new Options());
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @param options AeroGear initialization options
     * @return MobileCore instance
     */
    public static void init(final Context context, final Options options)
                    throws InitializationException {
        instance = new MobileCore(context, options);
    }

    public static MobileCore getInstance() {
        return instance;
    }

    /**
     * Called when mobile core instance needs to be destroyed
     */
    public static void destroy() {
        for (Class<? extends ServiceModule> serviceKey : instance.services.keySet()) {
            ServiceModule serviceModule = instance.services.get(serviceKey);
            serviceModule.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceModule> T getInstance(final Class<T> serviceClass) {
        return getInstance(serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServiceModule> T getInstance(final Class<T> serviceClass,
                    final ServiceConfiguration serviceConfiguration)
                    throws InitializationException {
        nonNull(serviceClass, "serviceClass");

        if (instance.services.containsKey(serviceClass)) {
            return (T) instance.services.get(serviceClass);
        }

        try {
            final ServiceModule serviceModule = serviceClass.newInstance();

            ServiceConfiguration serviceCfg = serviceConfiguration;

            if (serviceCfg == null) {
                serviceCfg = instance.getServiceConfiguration(serviceModule.type());
            }

            if (serviceCfg == null && serviceModule.requiresConfiguration()) {
                throw new ConfigurationNotFoundException(
                                serviceModule.type() + " not found on " + instance.configFileName);
            }

            serviceModule.configure(instance, serviceCfg);

            instance.services.put(serviceClass, serviceModule);

            return (T) serviceModule;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    /**
     * Get application context
     *
     * @return Application context
     */

    public Context getContext() {
        return instance.context;
    }

    /**
     * Returns the configuration for this singleThreadService from the JSON config file
     *
     * @param type Service type/name
     * @return the configuration for this singleThreadService from the JSON config file
     */
    public ServiceConfiguration getServiceConfiguration(final String type) {
        return instance.servicesConfig.get(type);
    }

    /**
     * Get the user app version from the package manager
     *
     * @param context Android application context
     * @return String app version name
     */
    private String getAppVersion(final Context context) throws InitializationException {
        nonNull(context, "context");
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                            0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Wrap in Initialization exception
            throw new InitializationException("Failed to read app version", e);
        }
    }

    public HttpServiceModule getHttpLayer() {
        return instance.httpLayer;
    }

    public Logger getLogger() {
        return instance.logger;
    }

    @VisibleForTesting()
    public String getConfigFileName() {
        return instance.configFileName;
    }

    /**
     * Get the version name of the SDK itself
     *
     * @return String SDK version
     */
    public String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * Get the version of the user app
     *
     * @return String App version name
     */
    public String getAppVersion() {
        return instance.appVersion;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public static final class Options {

        private String configFileName;
        private Logger logger;
        private HttpServiceModule httpServiceModule;

        public Options() {
            this.configFileName = DEFAULT_CONFIG_FILE_NAME;
            this.logger = new LoggerAdapter();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
            this.httpServiceModule = new OkHttpServiceModule(builder.build());
        }

        public Options setConfigFileName(@NonNull final String configFileName) {
            this.configFileName = nonNull(configFileName, "configFileName");
            return this;
        }

        public Options setHttpServiceModule(@NonNull final HttpServiceModule httpServiceModule) {
            this.httpServiceModule = nonNull(httpServiceModule, "httpServiceModule");
            return this;
        }

        public Options setLogger(final Logger logger) {
            this.logger = logger;
            return this;
        }
    }
}
