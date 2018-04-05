package org.aerogear.mobile.core.http;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Requester;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

/**
 * This is a implementation of HttpRequest based on OKHttp
 */
public class OkHttpRequest implements HttpRequest {

    private final OkHttpClient client;
    private final AppExecutors appExecutors;
    private final Map<String, String> headers = new HashMap<>();
    private Call call;

    public OkHttpRequest(final OkHttpClient client, final AppExecutors executors) {
        this.client = client;
        this.appExecutors = executors;
        this.headers.put(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
    }


    public HttpRequest addHeader(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public org.aerogear.mobile.core.reactive.Request<? extends HttpResponse> get(final String url) {
        AtomicReference<OkHttpResponse> response = new AtomicReference<>();
        return Requester.call(() -> {
            System.out.println("Http call on" + Thread.currentThread());
            Builder getRequestBuilder = requestBuilderWithUrl(url);
            addHeaders(getRequestBuilder);
            Request getRequest = getRequestBuilder.build();
            call = client.newCall(getRequest);

            OkHttpResponse okHttpResponse = new OkHttpResponse(call);
            response.set(okHttpResponse);
            return okHttpResponse;
        }, () -> {
            /* Cleaner */
            System.out.println("Cleanup call on " + Thread.currentThread());
            OkHttpResponse okHttpResponse = response.get();
            if (okHttpResponse != null && !okHttpResponse.isClosed()) {
                okHttpResponse.waitForCompletionAndClose();
            }
        }).cancelWith(() -> {
            call.cancel();
        }).requestOn(appExecutors.networkThread());

    }

    @Override
    public org.aerogear.mobile.core.reactive.Request<OkHttpResponse> post(final String url,
                    final byte[] body) {

        AtomicReference<OkHttpResponse> response = new AtomicReference<>();
        return Requester.call(() -> {
            RequestBody requestBody = RequestBody
                            .create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
            Request.Builder postRequestBuilder = requestBuilderWithUrl(url).post(requestBody);
            addHeaders(postRequestBuilder);

            Request postRequest = postRequestBuilder.build();
            call = client.newCall(postRequest);

            OkHttpResponse okHttpResponse = new OkHttpResponse(call);
            response.set(okHttpResponse);
            return okHttpResponse;
        }, () -> {
            /* Cleaner */
            OkHttpResponse okHttpResponse = response.get();
            if (okHttpResponse != null && !okHttpResponse.isClosed()) {
                okHttpResponse.waitForCompletionAndClose();
            }
        }).cancelWith(() -> {
            call.cancel();
        }).requestOn(appExecutors.networkThread());
    }

    @Override
    public org.aerogear.mobile.core.reactive.Request<OkHttpResponse> put(final String url,
                    final byte[] body) {

        AtomicReference<OkHttpResponse> response = new AtomicReference<>();
        return Requester.call(() -> {
            RequestBody requestBody = RequestBody
                            .create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
            Request.Builder postRequestBuilder = requestBuilderWithUrl(url).put(requestBody);
            addHeaders(postRequestBuilder);

            Request postRequest = postRequestBuilder.build();
            call = client.newCall(postRequest);

            OkHttpResponse okHttpResponse = new OkHttpResponse(call);
            response.set(okHttpResponse);
            return okHttpResponse;
        }, () -> {
            /* Cleaner */
            OkHttpResponse okHttpResponse = response.get();
            if (okHttpResponse != null && !okHttpResponse.isClosed()) {
                okHttpResponse.waitForCompletionAndClose();
            }
        }).cancelWith(() -> {
            call.cancel();
        }).requestOn(appExecutors.networkThread());

    }

    @Override
    public org.aerogear.mobile.core.reactive.Request<OkHttpResponse> delete(final String url) {

        AtomicReference<OkHttpResponse> response = new AtomicReference<>();
        return Requester.call(() -> {
            Request.Builder deleteRequestBuilder = requestBuilderWithUrl(url).delete();
            addHeaders(deleteRequestBuilder);

            Request deleteRequest = deleteRequestBuilder.build();
            call = client.newCall(deleteRequest);

            OkHttpResponse okHttpResponse = new OkHttpResponse(call);
            response.set(okHttpResponse);
            return okHttpResponse;
        }, () -> {
            /* Cleaner */
            OkHttpResponse okHttpResponse = response.get();
            if (okHttpResponse != null && !okHttpResponse.isClosed()) {
                okHttpResponse.waitForCompletionAndClose();
            }
        }).cancelWith(() -> {
            call.cancel();
        }).requestOn(appExecutors.networkThread());
    }

    private Request.Builder requestBuilderWithUrl(final String url) {
        return new Request.Builder().url(url);
    }

    private void addHeaders(final Request.Builder requestBuilder) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }
    }
}
