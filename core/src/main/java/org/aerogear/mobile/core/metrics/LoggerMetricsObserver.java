package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.interfaces.MetricsPublisher;
import org.aerogear.mobile.core.metrics.interfaces.Observer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Used if no metrics configuration is provided. All metrics data will be
 * logged only
 */
public final class LoggerMetricsObserver implements Observer {
    private final Logger logger;

    public LoggerMetricsObserver(final Logger logger) {
        this.logger = logger;
    }

    public MetricsPublisher getObservableForNamespace(final String namespace) {
        return new MetricsProducer(namespace, this);
    }

    @Override
    public void onData(final String namespace, final Map<String, String> data) {
        try {
            JSONObject namespacedMetrics = new JSONObject();
            namespacedMetrics.put(namespace, data);
            logger.debug(MetricsService.TAG, data.toString());
        } catch (JSONException e) {
            logger.error(MetricsService.TAG, e);
        }
    }
}
