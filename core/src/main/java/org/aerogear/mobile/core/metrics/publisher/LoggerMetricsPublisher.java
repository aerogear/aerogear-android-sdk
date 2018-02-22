package org.aerogear.mobile.core.metrics.publisher;

import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * All metrics data will be logged only
 */
public final class LoggerMetricsPublisher implements MetricsPublisher {

    private final Logger logger;

    public LoggerMetricsPublisher(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(final Metrics... metrics) {
        nonNull(metrics, "metrics");
        for (final Metrics m : metrics) {
            logger.debug("Metrics -> [" + m.identifier() + "]:" + m.data().toString());
        }
    }
}
