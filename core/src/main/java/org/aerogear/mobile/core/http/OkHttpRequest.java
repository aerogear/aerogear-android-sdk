package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.executor.AppExecutors;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        client.setConnectTimeout(60, TimeUnit.SECONDS)
        this.appExecutors = executors;
        this.headers.put(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
    }

    @Override
    public HttpRequest addHeader(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public void get(final String url) {
        Request.Builder getRequestBuilder = requestBuilderWithUrl(url);
        addHeaders(getRequestBuilder);
        Request getRequest = getRequestBuilder.build();
        call = client.newCall(getRequest);
    }

    @Override
    public void post(final String url, final byte[] body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
        Request.Builder postRequestBuilder = requestBuilderWithUrl(url).post(requestBody);
        addHeaders(postRequestBuilder);

        Request postRequest= postRequestBuilder.build();
        call = client.newCall(postRequest);
    }

    @Override
    public void put(final String url, final byte[] body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
        Request.Builder postRequestBuilder = requestBuilderWithUrl(url).put(requestBody);
        addHeaders(postRequestBuilder);

        Request postRequest= postRequestBuilder.build();
        call = client.newCall(postRequest);
    }

    @Override
    public void delete(final String url) {
        Request.Builder deleteRequestBuilder = requestBuilderWithUrl(url).delete();
        addHeaders(deleteRequestBuilder);

        Request deleteRequest = deleteRequestBuilder.build();
        call = client.newCall(deleteRequest);
    }

    @Override
    public HttpResponse execute() {
        return new OkHttpResponse(call, appExecutors);
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
