package org.aerogear.mobile.core.metrics;


import org.aerogear.mobile.core.http.HttpResponse;

public interface MetricsPublisherListener {

    void onPublishMetricsSuccess(HttpResponse response);

    void onPublishMetricsError(Exception error);
}
