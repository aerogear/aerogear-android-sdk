package org.aerogear.mobile.core;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.aerogear.android.core.BuildConfig;
import org.aerogear.mobile.core.configuration.MobileCoreJsonConfig;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpCertificatePinning;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;

import okhttp3.OkHttpClient;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    public static final String DEFAULT_CONFIG_FILE_NAME = "mobile-services.json";

    private static final String TAG = "AEROGEAR/CORE";
    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_WRITE_TIMEOUT = 10;
    private static Logger logger = new LoggerAdapter();
    private static String appVersion;

    private final Context context;
    private final String configFileName;
    private final HttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> servicesConfig;
    private final Map<String, String> httpsConfig;
    private final Map<Class<? extends ServiceModule>, ServiceModule> services = new HashMap<>();

    /**
     * Creates a MobileCore instance
     *
     * @param context Application context
     */
    private MobileCore(final Context context, final Options options)
                    throws InitializationException, IllegalStateException {
        this.context = nonNull(context, "context").getApplicationContext();
        this.configFileName = nonNull(options, "options").configFileName;

        // -- Allow to override the default logger
        if (options.logger != null) {
            logger = options.logger;
        }

        // -- Parse JSON config file
        try (final InputStream configStream = context.getAssets().open(configFileName)) {
            MobileCoreJsonConfig jsonConfig = MobileCoreJsonConfig.produce(configStream);
            httpsConfig = jsonConfig.getCertificatePinningHashes();
            servicesConfig = jsonConfig.getServicesConfig();
            configStream.close();
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new InitializationException(message, exception);
        }

        // -- Set the app version variable
        appVersion = getAppVersion(context);

        // -- Setting default http layer
        if (options.httpServiceModule == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            OkHttpCertificatePinning certificatePinning = new OkHttpCertificatePinning(httpsConfig);
            builder.certificatePinner(certificatePinning.pinCertificates());

            builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
            final OkHttpServiceModule httpServiceModule = new OkHttpServiceModule(builder.build());
            ServiceConfiguration configuration = this.servicesConfig.get(httpServiceModule.type());
            if (configuration == null) {
                configuration = new ServiceConfiguration.Builder().build();
            }

            httpServiceModule.configure(this, configuration);

            this.httpLayer = httpServiceModule;
        } else {
            this.httpLayer = options.httpServiceModule;
        }
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @return MobileCore instance
     */
    public static MobileCore init(final Context context) throws InitializationException {
        return init(context, new Options());
    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     * @param options AeroGear initialization options
     * @return MobileCore instance
     */
    public static MobileCore init(final Context context, final Options options)
                    throws InitializationException {
        return new MobileCore(context, options);
    }

    /**
     * Called when mobile core instance needs to be destroyed
     */
    public void destroy() {
        for (Class<? extends ServiceModule> serviceKey : services.keySet()) {
            ServiceModule serviceModule = services.get(serviceKey);
            serviceModule.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getInstance(final Class<T> serviceClass) {
        return (T) getInstance(serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getInstance(final Class<T> serviceClass,
                    final ServiceConfiguration serviceConfiguration)
                    throws InitializationException {
        nonNull(serviceClass, "serviceClass");

        if (services.containsKey(serviceClass)) {
            return (T) services.get(serviceClass);
        }

        try {
            final ServiceModule serviceModule = serviceClass.newInstance();

            ServiceConfiguration serviceCfg = serviceConfiguration;

            if (serviceCfg == null) {
                serviceCfg = getServiceConfiguration(serviceModule.type());
            }

            if (serviceCfg == null && serviceModule.requiresConfiguration()) {
                throw new ConfigurationNotFoundException(
                                serviceModule.type() + " not found on " + this.configFileName);
            }

            serviceModule.configure(this, serviceCfg);

            services.put(serviceClass, serviceModule);

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
        return context;
    }

    /**
     * Returns the configuration for this singleThreadService from the JSON config file
     *
     * @param type Service type/name
     * @return the configuration for this singleThreadService from the JSON config file
     */
    public ServiceConfiguration getServiceConfiguration(final String type) {
        return this.servicesConfig.get(type);
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
        return this.httpLayer;
    }

    public static Logger getLogger() {
        return logger;
    }

    @VisibleForTesting()
    public String getConfigFileName() {
        return configFileName;
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

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public static final class Options {

        private String configFileName = DEFAULT_CONFIG_FILE_NAME;
        // Don't have a default implementation because it should use configuration
        private HttpServiceModule httpServiceModule;
        private Logger logger = new LoggerAdapter();

        public Options() {}

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
