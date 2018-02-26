package org.aerogear.mobile.core.metrics;

public interface MetricsPublisher {

    /**
     * Allows to publish metrics to external source
     * 
     * @param metrics a array of metrics objects to publish
     */
    void publish(Metrics... metrics);

}
