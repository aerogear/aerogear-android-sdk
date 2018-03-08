package org.aerogear.mobile.core.metrics;

public interface MetricsPublisher {

    /**
     * Allows to publish metrics to external source
     *
     * @param listener a callback listener to handle the result of a publication
     * @param metrics a array of metrics objects to publish
     */
    void publish(MetricsPublisherListener listener, Metrics... metrics);

}
