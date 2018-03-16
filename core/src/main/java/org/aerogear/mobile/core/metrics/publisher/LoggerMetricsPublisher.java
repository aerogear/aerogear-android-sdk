package org.aerogear.mobile.core.metrics.publisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.Nullable;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;

/**
 * All metrics data will be logged only
 */
public final class LoggerMetricsPublisher implements MetricsPublisher {

    private final Logger logger;

    public LoggerMetricsPublisher(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(final Metrics[] metrics, @Nullable final Callback callback) {
        nonNull(metrics, "metrics");
        for (final Metrics m : metrics) {
            logger.debug("Metrics -> [" + m.identifier() + "]:" + m.data().toString());
        }
        if (callback != null) {
            callback.onSuccess();
        }
    }
}
