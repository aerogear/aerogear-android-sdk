package org.aerogear.mobile.core.http.interceptors;

import java.io.IOException;

import okhttp3.Request;

/**
 * Interceptor interface that could be used to append data to request.
 */
public interface RequestInterceptor {

    /**
     * Decorate builder with additional headers and arguments that are needed
     * @param builder - builder object
     * @return the same builder object
     */
    Request.Builder decorate(Request.Builder builder);
}