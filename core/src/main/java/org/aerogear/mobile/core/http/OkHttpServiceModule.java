package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.interceptors.RequestHeaderInterceptor;

import okhttp3.OkHttpClient;

public class OkHttpServiceModule implements HttpServiceModule {

    private final OkHttpClient client;
    private RequestHeaderInterceptor interceptor;

    /**
     * This is the default no argument constructor for all ServiceModules.
     */
    public OkHttpServiceModule() {
        this(new OkHttpClient.Builder().build());
    }

    /**
     * @param client a default OkHttpClient instance to use.
     */
    public OkHttpServiceModule(final OkHttpClient client) {
        this.client = client;
    }

    public OkHttpServiceModule(OkHttpClient client, RequestHeaderInterceptor dynamicInterceptor) {
        this.client = client;
        this.interceptor = dynamicInterceptor;
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

    /**
     * Returns manager that adds new interceptor to the chain of core http interceptors that could
     * be used to add headers to network requests
     *
     * @return RequestHeaderInterceptor
     */
    public RequestHeaderInterceptor requestHeaderInterceptor() {
        return interceptor;
    }
}
