package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;

import okhttp3.OkHttpClient;

public class OkHttpServiceModule implements HttpServiceModule {

    private OkHttpClient client;
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
    public OkHttpServiceModule(OkHttpClient client) {
        this(client, new ServiceConfiguration.Builder().build());
    }

    public OkHttpServiceModule(ServiceConfiguration serviceConfiguration) {
        this(new OkHttpClient.Builder().build(), serviceConfiguration);
    }

    public OkHttpServiceModule(OkHttpClient client, ServiceConfiguration serviceConfiguration) {
        this.client = client;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public String type() {
        return "http";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public void destroy() {
    }

    @Override
    public HttpRequest newRequest() {
        OkHttpRequest request = new OkHttpRequest(client, new AppExecutors());
        return request;
    }

}
