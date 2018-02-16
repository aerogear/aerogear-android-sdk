package org.aerogear.mobile.core.metrics.interfaces;

import org.json.JSONObject;

import java.util.Map;

public interface MetricsPublisher {
    String namespace();
    void pushMetrics(final Map<String, String> data);
}
