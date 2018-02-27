package org.aerogear.mobile.core.metrics;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.impl.AppMetrics;
import org.aerogear.mobile.core.metrics.impl.DeviceMetrics;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

public class MetricsService implements ServiceModule {

    private Metrics[] defaultMetrics;
    private MetricsPublisher publisher;

    public MetricsPublisher getPublisher() {
        return publisher;
    }

    public MetricsService setPublisher(@NonNull final MetricsPublisher publisher) {
        this.publisher = nonNull(publisher, "publisher");
        return this;
    }

    @Override
    public String type() {
        return "metrics";
    }

    @Override
    public void configure(@NonNull final MobileCore core, @NonNull final ServiceConfiguration serviceConfiguration) {
        nonNull(core, "mobileCore");
        nonNull(serviceConfiguration, "serviceConfiguration");

        defaultMetrics = new Metrics[]{
            new AppMetrics(core.getContext()),
            new DeviceMetrics(core.getContext())
        };

        final String metricsUrl = serviceConfiguration.getUrl();
        if (metricsUrl == null) {
            publisher = new LoggerMetricsPublisher(MobileCore.getLogger());
        } else {
            publisher = new NetworkMetricsPublisher(core.getContext(), core.getHttpLayer().newRequest(), metricsUrl);
        }
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {
    }

    /**
     * Send default metrics
     */
    public void sendAppAndDeviceMetrics() {
        if (publisher == null) {
            throw new IllegalStateException("Make sure you have called configure or get this instance from MobileCore.getInstance()");
        }
        publisher.publish(defaultMetrics);
    }

    /**
     * Send metrics
     *
     * @param metrics Metrics to send
     * @return this MetricsService instance
     */
    public MetricsService publish(final Metrics... metrics) {
        nonNull(metrics, "metrics");
        if (publisher == null) {
            throw new IllegalStateException("Make sure you have called configure or get this instance from MobileCore.getInstance()");
        }
        publisher.publish(metrics);
        return this;
    }

}
