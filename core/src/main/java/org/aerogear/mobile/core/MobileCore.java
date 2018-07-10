package org.aerogear.mobile.core;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
import org.aerogear.mobile.core.http.OkHttpCertificatePinningParser;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;
import org.aerogear.mobile.core.metrics.MetricsService;

import okhttp3.OkHttpClient;

/**
 * MobileCore is the entry point into AeroGear mobile services
 */
public final class MobileCore {

    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_WRITE_TIMEOUT = 10;

    private static final String TAG = "MobileCore";

    @SuppressLint("StaticFieldLeak")
    private static MobileCore instance;
    // TODO Move to instance
    private static Logger logger = new LoggerAdapter();

    private final Context context;
    private final String appVersion;
    private final String configFileName = "mobile-services.json";
    private final OkHttpServiceModule httpLayer;
    private final Map<String, ServiceConfiguration> serviceConfigById;
    private final Map<String, List<ServiceConfiguration>> serviceConfigsByType;
    private final MetricsService metricsService;

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
            serviceConfigById = jsonConfig.getServicesConfigPerId();
            serviceConfigsByType = jsonConfig.getServiceConfigsPerType();
        } catch (JSONException | IOException exception) {
            String message = String.format("%s could not be loaded", configFileName);
            throw new InitializationException(message, exception);
        }

        // -- HTTP layer --------------------------------------------------------------------------
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        OkHttpCertificatePinningParser certificatePinning =
                        new OkHttpCertificatePinningParser(httpsConfig.getCertPinningConfig());
        builder.certificatePinner(certificatePinning.parse());

        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        final OkHttpServiceModule httpServiceModule = new OkHttpServiceModule(builder.build());
        ServiceConfiguration configuration =
                        this.getServiceConfigurationByType(httpServiceModule.type());
        if (configuration == null) {
            configuration = new ServiceConfiguration.Builder().build();
        }

        httpServiceModule.configure(this, configuration);

        this.httpLayer = httpServiceModule;

        // Metrics Service ------------------------------------------------------------------------

        ServiceConfiguration metricsConfig = getServiceConfigurationByType("metrics");
        if (metricsConfig != null) {
            metricsService = new MetricsService(metricsConfig.getUrl());
        } else {
            metricsService = null;
        }

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

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getService(final Class<T> serviceClass) {
        return getService(serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServiceModule> T getService(final Class<T> serviceClass,
                    final ServiceConfiguration serviceConfiguration)
                    throws InitializationException {
        nonNull(serviceClass, "serviceClass");

        try {
            final ServiceModule serviceModule = serviceClass.newInstance();

            ServiceConfiguration serviceCfg = serviceConfiguration;

            if (serviceCfg == null) {
                serviceCfg = this.getServiceConfigurationByType(serviceModule.type());
            }

            if (serviceCfg == null && serviceModule.requiresConfiguration()) {
                throw new ConfigurationNotFoundException(
                                serviceModule.type() + " not found on " + configFileName);
            }

            serviceModule.configure(this, serviceCfg);

            return (T) serviceModule;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve Metrics Services
     *
     * @return Metrics Service
     *
     * @throws ConfigurationNotFoundException throw if metrics is not enable
     */
    public MetricsService getMetricsService() throws ConfigurationNotFoundException {
        if (metricsService == null) {
            throw new ConfigurationNotFoundException("metrics not found on " + configFileName);
        }
        return metricsService;
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
    public OkHttpServiceModule getHttpLayer() {
        return httpLayer;
    }

    /**
     * Returns the configurations for this service type from the JSON config file.
     *
     * @param type Service type
     * @return the configurations for this service type from the JSON config file
     */
    public List<ServiceConfiguration> getServiceConfigurationsByType(final String type) {
        nonNull(type, "type");
        return serviceConfigsByType.get(type.toLowerCase());
    }

    /**
     * Returns the configuration for this service type from the JSON config file.
     * <p>
     * If there are multiple configs for the type, the first one will be returned.
     *
     * @param type Service type
     * @return the first configuration for this service type from the JSON config file
     */
    public ServiceConfiguration getServiceConfigurationByType(final String type) {
        nonNull(type, "type");
        final List<ServiceConfiguration> configs = serviceConfigsByType.get(type.toLowerCase());
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        if (configs.size() > 1) {
            logger.warning(TAG, "There are multiple configs for the service type " + type
                            + ". Using the first one found.");
        }
        return configs.get(0);
    }

    /**
     * Returns the configuration for this service from the JSON config file by the service id.
     *
     * @param id Service id
     * @return the configuration for this service id from the JSON config file
     */
    public ServiceConfiguration getServiceConfigurationById(final String id) {
        return serviceConfigById.get(id);
    }

}
