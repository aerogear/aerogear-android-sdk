package org.aerogear.mobile.core.metrics;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.publisher.LoggerMetricsPublisher;
import org.aerogear.mobile.core.metrics.publisher.NetworkMetricsPublisher;

public class MetricsService implements ServiceModule {

    private static final String INIT_METRICS_TYPE = "init";

    private static final Metrics[] EMPTY_METRICS = new Metrics[0];

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
    public void configure(@NonNull final MobileCore core,
                    @NonNull final ServiceConfiguration serviceConfiguration) {
        nonNull(core, "mobileCore");
        nonNull(serviceConfiguration, "serviceConfiguration");

        final String metricsUrl = serviceConfiguration.getUrl();
        if (metricsUrl == null) {
            publisher = new LoggerMetricsPublisher(core.getContext());
        } else {
            publisher = new NetworkMetricsPublisher(core.getContext(),
                            core.getHttpLayer().newRequest(), metricsUrl);
        }
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {}

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
        if (publisher == null) {
            throw new IllegalStateException(
                            "Make sure you have called configure or get this instance from MobileCore.getService()");
        }

        publisher.publish(nonNull(type, "type"), nonNull(metrics, "metrics"), callback);
    }

}
