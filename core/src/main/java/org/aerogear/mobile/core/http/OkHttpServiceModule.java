package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;

import java.util.Map;

import okhttp3.OkHttpClient;

public class OkHttpServiceModule implements HttpServiceModule {

    private OkHttpClient client;
    private ServiceConfiguration httpServiceConfiguration;

    /**
     * This is the default no argument constructor for all ServiceModules.
     */
    public OkHttpServiceModule() {
        this.httpServiceConfiguration = ServiceConfiguration.newConfiguration().build();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        this.client = clientBuilder.build();
    }

    /**
     * This constructer uses a specific client for manual configurations and testing.
     * @param client a default OkHttpClient instance to use.
     */
    public OkHttpServiceModule(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void bootstrap(final MobileCore core, final ServiceConfiguration configuration) {
        this.httpServiceConfiguration = configuration;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        this.client = clientBuilder.build();
    }

    @Override
    public HttpRequest newRequest() {
        OkHttpRequest request = new OkHttpRequest(client, new AppExecutors());
        for (Map.Entry<String, String> header : httpServiceConfiguration.getHeaders().entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
        return request;
    }

}
