package org.aerogear.mobile.core.http;

import android.util.Log;

import org.aerogear.mobile.core.executor.AppExecutors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This is a implementation of HttpRequest based on OKHttp
 */

public class OkHttpRequest implements HttpRequest {

    private final OkHttpClient client;
    private final AppExecutors appExecutors;
    private Map<String, String> headers = new HashMap<>();
    private Call call;



    public OkHttpRequest(OkHttpClient client, AppExecutors executors) {
        this.client = client;
        this.appExecutors = executors;
        this.headers.put(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
    }

    @Override
    public HttpRequest addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public void get(String url) {

        Request.Builder getRequestBuilder = new Request.Builder().url(url);

        addHeaders(getRequestBuilder);

        Request getRequest = getRequestBuilder.build();

        call = client.newCall(getRequest);
    }

    @Override
    public void post(String url, byte[] body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
        Request.Builder postRequestBuilder = new Request.Builder().url(url).post(requestBody);

        addHeaders(postRequestBuilder);

        Request postRequest= postRequestBuilder.build();
        call = client.newCall(postRequest);
    }

    private void addHeaders(Request.Builder requestBuilder) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }
    }

    @Override
    public void put(String url, byte[] body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(headers.get(CONTENT_TYPE_HEADER)), body);
        Request.Builder postRequestBuilder = new Request.Builder().url(url).put(requestBody);

        addHeaders(postRequestBuilder);

        Request postRequest= postRequestBuilder.build();
        call = client.newCall(postRequest);
    }

    @Override
    public void delete(String url) {
        Request.Builder deleteRequestBuilder = new Request.Builder().url(url).delete();

        addHeaders(deleteRequestBuilder);

        Request deleteRequest = deleteRequestBuilder.build();
        call = client.newCall(deleteRequest);
    }

    @Override
    public HttpResponse execute() {
        return new OkHttpResponse(call, appExecutors);
    }
}
