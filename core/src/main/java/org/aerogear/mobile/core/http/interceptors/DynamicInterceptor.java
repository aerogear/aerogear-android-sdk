package org.aerogear.mobile.core.http.interceptors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Dynamic interceptor that wraps OK HTTP and allows to reload chain of request interceptors
 * Class can be used to dynamically decorate requests
 *
 * @see RequestInterceptor - interface used as decorator.
 */
public class DynamicInterceptor implements Interceptor {

    private List<RequestInterceptor> interceptors;

    public DynamicInterceptor() {
        interceptors = new LinkedList<>();
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        // Go with all dynamic interceptors
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.decorate(builder);
        }
        return chain.proceed(builder.build());
    }

    public void add(RequestInterceptor interceptors) {
        this.interceptors.add(interceptors);
    }

    public void reset() {
        interceptors = new LinkedList<>();
    }
}




