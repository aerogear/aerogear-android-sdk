package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;

public class MetricsService implements ServiceModule {

    private Metrics[] defaultMetrics;
    private MetricsPublisher publisher;

    public MetricsPublisher getPublisher() {
        return publisher;
    }

    public MetricsService setPublisher(MetricsPublisher publisher) {
        if (publisher == null) {
            throw new IllegalStateException("publisher should not be null");
        }
        this.publisher = publisher;
        return this;
    }

    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        defaultMetrics = new Metrics[]{
            new AppMetrics(core.getContext()),
            new DeviceMetrics(core.getContext())
        };

        String metricsUrl = serviceConfiguration.getUrl();
        if (metricsUrl == null) {
            publisher = new LoggerMetricsPublisher(MobileCore.getLogger());
        } else {
            publisher = new NetworkMetricsPublisher(core.getContext(), core.getHttpLayer().newRequest(), metricsUrl);
        }
    }

    @Override
    public boolean requiresConfiguration() { return true; }

    @Override
    public void destroy() {
    }

    /**
     * Send default metrics
     */
    public void sendDefaultMetrics() {
        if (publisher == null) {
            throw new IllegalStateException("Make sure you have called configure or get this instance from MobileCore.getInstance()");
        }
        publisher.publish(defaultMetrics);
    }

    /**
     * Send metrics
     *
     * @param metrics Metrics to send
     */
    public MetricsService publish(Metrics... metrics) {
        if (publisher == null) {
            throw new IllegalStateException("Make sure you have called configure or get this instance from MobileCore.getInstance()");
        }
        publisher.publish(metrics);
        return this;
    }

}
