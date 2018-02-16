package org.aerogear.mobile.core.metrics;

import org.aerogear.mobile.core.metrics.interfaces.Observer;
import org.aerogear.mobile.core.metrics.interfaces.MetricsPublisher;
import org.json.JSONObject;

import java.util.Map;

/**
 * MetricsProducer is the default implementation of a MetricsPublisher that
 * forwards any data to it's observer under. It also keeps track of the
 * namespace that the metrics are published under.
 */
public final class MetricsProducer implements MetricsPublisher {
    private final String namespace;
    private final Observer observer;

    public MetricsProducer(final String namespace, final Observer observer) {
        this.namespace = namespace;
        this.observer = observer;
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public void pushMetrics(final Map<String, String> data) {
        observer.onData(namespace, data);
    }
}
