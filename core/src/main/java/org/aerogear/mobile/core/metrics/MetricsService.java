package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.interfaces.MetricsPublisher;
import org.aerogear.mobile.core.metrics.interfaces.Observer;
import org.json.JSONException;

public class MetricsService implements ServiceModule {
    public final static String TAG = "AEROGEAR/METRICS";
    private DefaultMetrics defaultMetrics;
    private Observer observer;

    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        defaultMetrics = new DefaultMetrics(core.getContext());
        String metricsUrl = serviceConfiguration.getUrl();
        if (metricsUrl == null) {
            observer = new LoggerMetricsObserver(MobileCore.getLogger());
        } else {
            observer = new NetworkMetricsObserver(core, metricsUrl);
        }
    }


    /**
     * Returns a namespaced observable that can be used to publish metrics
     * under the given namespace
     *
     * @param namespace The name of the object the metrics are wrapped into
     * @return Observable an object that allows to send metrics
     */
    public MetricsPublisher getPublisherForNamespace(final String namespace) {
        return observer.getObservableForNamespace(namespace);
    }

    /**
     * Send default metrics including:
     * - Client ID
     * - App ID
     * - App version
     * - SDK version
     * - Platform (always "android")
     * - Platform version
     */
    public void sendDefaultMetrics() {
        try {
            getPublisherForNamespace("default")
                .pushMetrics(defaultMetrics.getDefaultMetrics());
        } catch (JSONException e) {
            MobileCore.getLogger().error(TAG, e);
        }
    }

    @Override
    public void destroy() {
    }
}
