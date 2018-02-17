package org.aerogear.mobile.core.metrics;

public interface MetricsPublisher {

    /**
     * Allows to publish metrics to external source
     */
    void publish(Metrics... metrics);

}
