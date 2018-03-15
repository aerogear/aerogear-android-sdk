package org.aerogear.mobile.core.metrics.publisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;
import org.aerogear.mobile.core.utils.ClientIdGenerator;

/**
 * Sends metrics data to the backend using the configuration in JSON config file
 */
public class NetworkMetricsPublisher implements MetricsPublisher {

    private final Context context;
    private final HttpRequest httpRequest;
    private final String url;

    public NetworkMetricsPublisher(final Context context, final HttpRequest httpRequest,
                    final String url) {
        this.context = context;
        this.httpRequest = httpRequest;
        this.url = url;
    }

    @Override
    public void publish(final Metrics[] metrics, Callback callback) {
        nonNull(metrics, "metrics");

        try {
            final JSONObject json = new JSONObject();

            json.put("clientId", ClientIdGenerator.getOrCreateClientId(context));
            json.put("timestamp", System.currentTimeMillis());

            final JSONObject data = new JSONObject();
            for (final Metrics m : metrics) {
                data.put(m.identifier(), m.data());
            }

            json.put("data", data);

            httpRequest.post(url, json.toString().getBytes());

            MobileCore.getLogger().debug("Sending metrics");

            final HttpResponse httpResponse = httpRequest.execute();
            httpResponse.onSuccess(() -> {
                if (callback != null) {
                    callback.onSuccess();
                }
            }).onError(() -> {
                if (callback != null) {
                    callback.onError(httpResponse.getError());
                }
            });

        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
        }
    }
}
