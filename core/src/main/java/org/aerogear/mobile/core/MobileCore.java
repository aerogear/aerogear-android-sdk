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

import org.aerogear.android.core.BuildConfig;
import org.aerogear.mobile.core.configuration.MobileCoreConfiguration;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.configuration.https.HttpsConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.InitializationException;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpCertificatePinningParser;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;

import okhttp3.OkHttpClient;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_WRITE_TIMEOUT = 10;

    @SuppressLint("StaticFieldLeak")
    private static MobileCore instance;
    // TODO Move to instance
    private static Logger logger = new LoggerAdapter();

    private final Context context;
    private final String appVersion;
    private final String configFileName = "mobile-services.json";
    private final HttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> servicesConfig;
    private final Map<Class<? extends ServiceModule>, ServiceModule> services = new HashMap<>();

    /**
     * Get the user app version from the package manager
     *
     * @param context Android application context
     * @return String app version name
     */
    private String readAppVersion(final Context context) throws InitializationException {
        nonNull(context, "context");
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                            0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Wrap in Initialization exception
            throw new InitializationException("Failed to read app version", e);
        }
    }

    /**
     * Creates a MobileCore instance
     *
     * @param context Application context
     */
    private MobileCore(final Context context)
                    throws InitializationException, IllegalStateException {
        this.context = nonNull(context, "context").getApplicationContext();
        this.appVersion = readAppVersion(context);

        HttpsConfiguration httpsConfig;

        // -- Parse JSON config file
        try (final InputStream configStream = context.getAssets().open(configFileName)) {
            MobileCoreConfiguration jsonConfig = new MobileCoreJsonParser(configStream).parse();
            httpsConfig = jsonConfig.getHttpsConfig();
            servicesConfig = jsonConfig.getServicesConfig();
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new InitializationException(message, exception);
        }

        // Setup HTTP layer
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        OkHttpCertificatePinningParser certificatePinning =
                        new OkHttpCertificatePinningParser(httpsConfig.getCertPinningConfig());
        builder.certificatePinner(certificatePinning.parse());

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

    }

    /**
     * Initialize the AeroGear system
     *
     * @param context Application context
     */
    public static void init(final Context context) throws InitializationException {
        nonNull(context, "context");
        if (instance == null) {
            instance = new MobileCore(context);
        }
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
    public <T extends ServiceModule> T getService(final Class<T> serviceClass) {
        return getService(serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getService(final Class<T> serviceClass,
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
                                serviceModule.type() + " not found on " + configFileName);
            }

            serviceModule.configure(this, serviceCfg);

            services.put(serviceClass, serviceModule);

            return (T) serviceModule;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    /**
     * Get the logger
     *
     * @return Logger
     */
    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        MobileCore.logger = logger;
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
        return appVersion;
    }

    /**
     * Get the HTTP service module
     *
     * @return HTTP service module
     */
    public HttpServiceModule getHttpLayer() {
        return httpLayer;
    }

    /**
     * Returns the configuration for this singleThreadService from the JSON config file
     *
     * @param type Service type/name
     * @return the configuration for this singleThreadService from the JSON config file
     */
    public ServiceConfiguration getServiceConfiguration(final String type) {
        return servicesConfig.get(type);
    }

}
