package org.aerogear.mobile.metrics;

import org.aerogear.mobile.metrics.interfaces.MetricsProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * The metrics registry is the central storage for all metrics providers
 */
public final class MetricsRegistry {
    private static MetricsRegistry INSTANCE;

    private final Set<MetricsProvider> providers = new HashSet<>();

    private MetricsRegistry() {
        INSTANCE = this;
    }

    public void registerProvider(MetricsProvider provider) {
        providers.add(provider);
    }

    public Set<MetricsProvider> getProviders() {
        return this.providers;
    }

    public static MetricsRegistry instance() {
        if (INSTANCE == null) {
            new MetricsRegistry();
        }

        return INSTANCE;
    }
}
