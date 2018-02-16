package org.aerogear.mobile.core.metrics;

import java.util.Map;

public interface MetricsPublisher {
    String namespace();
    void pushMetrics(final Map<String, String> data);
}
