package org.aerogear.mobile.core.metrics;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.interfaces.MetricsPublisher;
import org.aerogear.mobile.core.metrics.interfaces.Observer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Sends metrics data to the backend using the configuration in
 * mobile-services.json
 */
public class NetworkMetricsObserver implements Observer {
    private final HttpServiceModule httpService;
    private final String metricsUrl;
    private final Context context;
    private final Logger logger;

    public NetworkMetricsObserver(final MobileCore core, final String metricsUrl) {
        this.httpService = core.getHttpLayer();
        this.logger = MobileCore.getLogger();
        this.context = core.getContext();
        this.metricsUrl = metricsUrl;
    }

    private void sendData(final String namespace, final Map<String, String> data)
        throws JSONException {
        JSONObject namespacedMetrics = new JSONObject();

        // The cast of the map to JSONObject is required because otherwise
        // the implementation would just use it's string representation
        namespacedMetrics.put(namespace, new JSONObject(data));

        // Send request to backend
        HttpRequest request = httpService.newRequest();
        if (request !=  null) {
            request.post(metricsUrl, namespacedMetrics.toString().getBytes());
            HttpResponse response = request.execute();

            // Async response handling
            response.onComplete(new MetricsResponseHandler(response));
        }
    }

    @Override
    public void onData(final String namespace, final Map<String, String> data) {
        try {
            sendData(namespace, data);
        } catch (JSONException e) {
            logger.error(MetricsService.TAG, e);
        }
    }

    @Override
    public MetricsPublisher getObservableForNamespace(final String namespace) {
        return new MetricsProducer(namespace, this);
    }

    private static final class MetricsResponseHandler implements Runnable {
        private final HttpResponse response;

        public MetricsResponseHandler(final HttpResponse response) {
            this.response = response;
        }

        @Override
        public void run() {
            if (response.requestFailed()) {
                MobileCore.getLogger().error(MetricsService.TAG, response.getRequestError());
            }
        }
    }
}
