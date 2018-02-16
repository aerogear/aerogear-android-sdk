package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.observer.LoggerMetricsObserver;
import org.aerogear.mobile.core.metrics.observer.NetworkMetricsObserver;
import org.aerogear.mobile.core.metrics.metrics.AppMetrics;
import org.aerogear.mobile.core.metrics.metrics.DeviceMetrics;

import java.util.ArrayList;
import java.util.List;

public class MetricsService implements ServiceModule {

    private List<Metrics> defaultMetrics = new ArrayList<>();
    private Observer observer;

    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        defaultMetrics.add(new AppMetrics(core.getContext()));
        defaultMetrics.add(new DeviceMetrics(core.getContext()));

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
     * Send default metrics
     */
    public void sendDefaultMetrics() {
        if(observer == null) {
            throw new IllegalStateException("Observer should not be null. Be sure you have called configure or retrive this from MobileCore.getInstance().");
        }
        for (Metrics metrics : defaultMetrics) {
            getPublisherForNamespace(metrics.identifier())
                .pushMetrics(metrics.data());
        }
    }

    @Override
    public void destroy() {
    }
}
