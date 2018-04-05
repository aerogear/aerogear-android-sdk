package org.aerogear.mobile.core.metrics.publisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import org.aerogear.mobile.core.reactive.Responder;
import org.json.JSONObject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.core.metrics.MetricsPublisher;
import org.aerogear.mobile.core.reactive.Responder;


/**
 * Sends metrics data to the backend using the configuration in JSON config file
 */
public final class NetworkMetricsPublisher extends MetricsPublisher {

    private static final Logger LOGGER = MobileCore.getLogger();

    private final HttpRequest httpRequest;
    private final String url;

    public NetworkMetricsPublisher(final Context context, final HttpRequest httpRequest,
                    final String url) {
        super(context);

        this.httpRequest = httpRequest;
        this.url = url;
    }

    @Override
    public void publish(@NonNull String type, @NonNull final Metrics[] metrics,
                    @Nullable final Callback callback) {
        nonNull(type, "type");
        nonNull(metrics, "metrics");

        final JSONObject json = createMetricsJSONObject(type, metrics);

        httpRequest.post(url, json.toString().getBytes());


            httpRequest.post(url, json.toString().getBytes())
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
                                        LOGGER.error(exception.getMessage());
                                    }
                                }
                            });

            LOGGER.debug("Sending metrics");

    }
}
