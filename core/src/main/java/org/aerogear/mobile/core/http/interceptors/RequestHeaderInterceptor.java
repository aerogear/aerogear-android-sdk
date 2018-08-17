package org.aerogear.mobile.core.http.interceptors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Dynamic request header interceptor that wraps OK HTTP and allows to reload chain of request providers
 * Class can be used to dynamically getHeaders requests
 *
 * @see HeaderProvider - interface used by clients to add headers
 */
public class RequestHeaderInterceptor implements Interceptor {

    private List<HeaderProvider> providers;

    public RequestHeaderInterceptor() {
        providers = new LinkedList<>();
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        // Process header providers
        for (HeaderProvider provider : providers) {
            Map<String, String> headers = provider.getHeaders();
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header));
            }
        }
        return chain.proceed(builder.build());
    }

    public void add(HeaderProvider headerProvider) {
        this.providers.add(headerProvider);
    }

    public void reset() {
        providers = new LinkedList<>();
    }
}




