package org.aerogear.mobile.core.metrics;

import java.util.Map;

public interface Observer {
    void onData(final String namespace, final Map<String, String> data);
    MetricsPublisher getObservableForNamespace(final String namespace);
}