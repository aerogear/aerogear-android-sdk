package org.aerogear.mobile.core.metrics;

import android.support.annotation.Nullable;

public interface MetricsPublisher {

    /**
     * Allows to publish metrics to external source
     *
     * @param metrics a array of metrics objects to publish
     * @param listener a callback listener to handle the result of a publication
     */
    void publish(Metrics[] metrics, @Nullable MetricsPublisherListener listener);

}
