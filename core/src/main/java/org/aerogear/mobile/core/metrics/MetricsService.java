package org.aerogear.mobile.core.metrics;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.exception.HttpException;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;
import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Requester;
import org.aerogear.mobile.core.utils.ClientIdGenerator;

public class MetricsService {

    private static final String INIT_METRICS_TYPE = "init";
    private static final Metrics[] EMPTY_METRICS = new Metrics[0];

    private final String url;

    public MetricsService(String url) {
        this.url = url;
    }

    /**
     * Send default metrics
     *
     * @return A {@link Request} boolean
     */
    public Request<Boolean> sendAppAndDeviceMetrics() {
        // as app and device metrics are added by the publisher
        // to the payload, we only pass empty metrics to publisher
        return this.publish(INIT_METRICS_TYPE, EMPTY_METRICS);
    }

    /**
     * Send metrics
     *
     * @param type type of the enclosing metrics event
     * @param metrics Metrics to send
     *
     * @return A {@link Request} boolean
     */
    public Request<Boolean> publish(@NonNull String type, @NonNull final Metrics... metrics) {

        return Requester.call(() -> MobileCore.getInstance().getHttpLayer().newRequest())
                        .requestMap(httpRequest -> httpRequest
                                        .post(url, createMetricsJSONObject(type, metrics).toString()
                                                        .getBytes())
                                        .requestMap(httpResponse -> Requester.call(() -> {
                                            switch (httpResponse.getStatus()) {
                                                case HTTP_NO_CONTENT:
                                                    return Boolean.TRUE;
                                                default:
                                                    throw (new HttpException(
                                                                    httpResponse.getStatus()));
                                            }
                                        })).requestOn(new AppExecutors().networkThread()));

    }

    private JSONObject createMetricsJSONObject(final String type, final Metrics[] metrics) {
        nonNull(type, "type");
        nonNull(metrics, "metrics");

        final JSONObject json = new JSONObject();

        try {

            json.put("clientId", ClientIdGenerator
                            .getOrCreateClientId(MobileCore.getInstance().getContext()));
            json.put("timestamp", System.currentTimeMillis());
            json.put("type", type);

            final JSONObject data = new JSONObject();

            Metrics[] defaultMetrics =
                            new Metrics[] {new AppMetrics(MobileCore.getInstance().getContext()),
                                            new DeviceMetrics()};

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
            MobileCore.getLogger().error(e.getMessage(), e);
        }

        return json;
    }

}
