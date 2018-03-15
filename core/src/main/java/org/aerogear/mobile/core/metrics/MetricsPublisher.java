package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.Callback;

public interface MetricsPublisher {

    /**
     * Allows to publish metrics to external source
     *
     * @param metrics a array of metrics objects to publish
     * @param callback callback of the publication
     */
    void publish(Metrics[] metrics, Callback callback);

}
