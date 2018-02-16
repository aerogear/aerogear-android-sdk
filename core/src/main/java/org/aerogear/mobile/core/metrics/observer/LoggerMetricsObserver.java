package org.aerogear.mobile.core.metrics.observer;

import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.MetricsPublisher;
import org.aerogear.mobile.core.metrics.Observer;
import org.aerogear.mobile.core.metrics.publisher.MetricsProducer;

import java.util.Map;

/**
 * Used if no metrics configuration is provided. All metrics data will be
 * logged only
 */
public final class LoggerMetricsObserver implements Observer {

    private final Logger logger;

    public LoggerMetricsObserver(Logger logger) {
        this.logger = logger;
    }

    public MetricsPublisher getObservableForNamespace(final String namespace) {
        return new MetricsProducer(namespace, this);
    }

    @Override
    public void onData(final String namespace, final Map<String, String> data) {
        logger.debug("Metrics -> [" + namespace + "]:" + data.toString());
    }

}
