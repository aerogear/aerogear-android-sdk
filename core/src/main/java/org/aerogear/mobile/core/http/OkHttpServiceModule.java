package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;

import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by summers on 1/23/18.
 */

public class OkHttpServiceModule implements HttpServiceModule {

    private OkHttpClient client;
    private HttpServiceConfiguration httpServiceConfiguration;

    /**
     * This is the default no argument constructor for all ServiceModules.
     */
    public OkHttpServiceModule() {
        this.httpServiceConfiguration = new HttpServiceConfiguration();
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
    public void bootstrap(MobileCore core, ServiceConfiguration configuration) {
        this.httpServiceConfiguration = new HttpServiceConfiguration(configuration);
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
