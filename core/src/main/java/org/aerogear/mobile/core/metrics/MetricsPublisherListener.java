package org.aerogear.mobile.core.metrics;

public interface MetricsPublisherListener {

    void onPublishMetricsSuccess();

    void onPublishMetricsError(Exception error);
}
