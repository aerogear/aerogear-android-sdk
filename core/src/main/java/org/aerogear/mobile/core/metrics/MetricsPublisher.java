package org.aerogear.mobile.core.metrics;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;
import org.aerogear.mobile.core.utils.ClientIdGenerator;

public abstract class MetricsPublisher {

    private static final Logger LOGGER = MobileCore.getLogger();

    private final Context context;
    private final Metrics[] defaultMetrics;

    public MetricsPublisher(final Context context) {
        this.context = context;

        defaultMetrics = new Metrics[] {new AppMetrics(context), new DeviceMetrics()};
    }

    /**
     * Parse metrics into a JSONObject and add communs information for all metrics requests:
     *
     * - clientId - timestamp - type - app - device
     *
     * @param type the type of metrics
     * @param metrics metrics
     * @return a JSONObject
     */
    protected JSONObject parseMetrics(final String type, final Metrics[] metrics) {
        nonNull(type, "type");
        nonNull(metrics, "metrics");

        final JSONObject json = new JSONObject();

        try {

            json.put("clientId", ClientIdGenerator.getOrCreateClientId(context));
            json.put("timestamp", System.currentTimeMillis());
            json.put("type", type);

            final JSONObject data = new JSONObject();

            // first put the default metrics (app and device info)
            for (final Metrics m : defaultMetrics) {
                data.put(m.identifier(), m.data());
            }

            // then put the specific ones
            for (final Metrics m : metrics) {
                data.put(m.identifier(), m.data());
            }

            json.put("data", data);

        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return json;

    }

    /**
     * Allows to publish metrics to external source
     *
     * @param type type of the enclosing metrics event
     * @param metrics a array of metrics objects to publish
     * @param callback callback of the publication
     */
    protected abstract void publish(@NonNull String type, @NonNull final Metrics[] metrics,
                    @Nullable final Callback callback);

}
