package org.aerogear.mobile.core.metrics.publisher;

import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;

/**
 * All metrics data will be logged only
 */
public final class LoggerMetricsPublisher implements MetricsPublisher {

    private final Logger logger;

    public LoggerMetricsPublisher(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(Metrics... metrics) {
        for (Metrics m : metrics) {
            logger.debug("Metrics -> [" + m.identifier() + "]:" + m.data().toString());
        }
    }
}
