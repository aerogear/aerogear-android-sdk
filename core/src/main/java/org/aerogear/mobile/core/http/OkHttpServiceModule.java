package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;

import okhttp3.OkHttpClient;

public class OkHttpServiceModule implements HttpServiceModule {

    private final OkHttpClient client;
    private ServiceConfiguration serviceConfiguration;

    /**
     * This is the default no argument constructor for all ServiceModules.
     */
    public OkHttpServiceModule() {
        this(new OkHttpClient.Builder().build());
    }

    /**
     * This constructer uses a specific client for manual configurations and testing.
     *
     * @param client a default OkHttpClient instance to use.
     */
    public OkHttpServiceModule(final OkHttpClient client) {
        this(client, new ServiceConfiguration.Builder().build());
    }

    public OkHttpServiceModule(final OkHttpClient client,
                    final ServiceConfiguration serviceConfiguration) {
        this.client = client;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public String type() {
        return "http";
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public OkHttpRequest newRequest() {
        return new OkHttpRequest(client, new AppExecutors());
    }

    /**
     * The client is a shared instance with all references from {@link MobileCore#getHttpLayer()}.
     * This client is configured with the AeroGear defaults, pinning, etc. during the MobileCore
     * initialization.
     *
     * @return the mobilecore okhttp instance.
     */
    public OkHttpClient getClient() {
        return client;
    }
}
