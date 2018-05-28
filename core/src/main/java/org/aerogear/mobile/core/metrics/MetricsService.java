package org.aerogear.mobile.core.metrics;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.core.utils.ClientIdGenerator;
import org.json.JSONException;
import org.json.JSONObject;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

public class MetricsService {

    private static final String INIT_METRICS_TYPE = "init";
    private static final Metrics[] EMPTY_METRICS = new Metrics[0];

    private final String url;

    public MetricsService() {
        ServiceConfiguration serviceConfiguration =
                        MobileCore.getInstance().getServiceConfigurationByType("metrics");
        this.url = serviceConfiguration.getUrl();
    }

    /**
     * Send default metrics
     */
    public void sendAppAndDeviceMetrics() {
        // as app and device metrics are added by the publisher
        // to the payload, we only pass empty metrics to publisher
        this.publish(INIT_METRICS_TYPE, EMPTY_METRICS, null);
    }

    /**
     * Send default metrics
     *
     * @param callback callback of the publication
     */
    public void sendAppAndDeviceMetrics(final Callback callback) {
        // as app and device metrics are added by the publisher
        // to the payload, we only pass empty metrics to publisher
        this.publish(INIT_METRICS_TYPE, EMPTY_METRICS, callback);
    }

    /**
     * Send metrics
     *
     * @param type type of the enclosing metrics event
     * @param metrics Metrics to send
     */
    public void publish(String type, Metrics... metrics) {
        publish(type, metrics, null);
    }

    /**
     * Send metrics
     *
     * @param type type of the enclosing metrics event
     * @param metrics Metrics to send
     * @param callback callback of the publication
     */
    public void publish(@NonNull String type, @NonNull final Metrics[] metrics,
                    final Callback callback) {

        final JSONObject json = createMetricsJSONObject(type, metrics);

        MobileCore.getInstance().getHttpLayer().newRequest().post(url, json.toString().getBytes())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse value) {
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                            }

                            @Override
                            public void onException(Exception exception) {
                                if (callback != null) {
                                    callback.onError(exception);
                                } else {
                                    MobileCore.getLogger().error(exception.getMessage());
                                }
                            }
                        });

        MobileCore.getLogger().debug("Sending metrics");

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
