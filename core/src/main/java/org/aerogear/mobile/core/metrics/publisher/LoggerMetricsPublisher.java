package org.aerogear.mobile.core.metrics.publisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;

/**
 * All metrics data will be logged only
 */
public final class LoggerMetricsPublisher implements MetricsPublisher {

    private final Logger logger;
    private Metrics[] defaultMetrics;

    public LoggerMetricsPublisher(final Logger logger, Context context) {
        this.logger = logger;
        this.defaultMetrics = new Metrics[] {new AppMetrics(context), new DeviceMetrics(context)};
    }

    @Override
    public void publish(@NonNull String type, @NonNull final Metrics[] metrics,
                    @Nullable final Callback callback) {
        nonNull(type, "type");
        nonNull(metrics, "metrics");
        logger.debug("Metrics: " + type);
        // first log the default metrics (app and device info)
        for (final Metrics m : defaultMetrics) {
            logger.debug("-> [" + m.identifier() + "]:" + m.data().toString());
        }
        // then log the specific metrics
        for (final Metrics m : metrics) {
            logger.debug("-> [" + m.identifier() + "]:" + m.data().toString());
        }
        if (callback != null) {
            callback.onSuccess();
        }
    }
}
