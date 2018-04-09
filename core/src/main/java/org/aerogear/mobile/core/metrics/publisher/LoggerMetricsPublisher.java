package org.aerogear.mobile.core.metrics.publisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.json.JSONObject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;

/**
 * All metrics data will be logged only
 */
public final class LoggerMetricsPublisher extends MetricsPublisher {

    private static final Logger LOGGER = MobileCore.getLogger();

    public LoggerMetricsPublisher(Context context) {
        super(context);
    }

    @Override
    public void publish(@NonNull String type, @NonNull final Metrics[] metrics,
                    @Nullable final Callback callback) {
        nonNull(type, "type");
        nonNull(metrics, "metrics");

        final JSONObject json = createMetricsJSONObject(type, metrics);

        LOGGER.debug(json.toString());

        if (callback != null) {
            callback.onSuccess();
        }
    }
}
