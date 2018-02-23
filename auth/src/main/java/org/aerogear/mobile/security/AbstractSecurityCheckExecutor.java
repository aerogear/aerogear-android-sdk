package org.aerogear.mobile.security;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.Collection;

/**
 * Base class for security check executors.
 */
abstract class AbstractSecurityCheckExecutor {

    /**
     * Collection of checks to be executed.
     */
    private final Collection<SecurityCheck> checks;

    /**
     * Context.
     */
    private final Context context;

    /**
     * Metric singleThreadService to be used to publish metrics. Can be null.
     */
    private final MetricsService metricsService;

    /**
     * Constructor.
     *
     * @param context the context
     * @param checks checks to be executed
     * @param metricService singleThreadService to be used to publish metrics. If null, no metrics get published.
     */
    public AbstractSecurityCheckExecutor(@NonNull final Context context,
                                         @NonNull final Collection<SecurityCheck> checks,
                                         @Nullable final MetricsService metricService) {
        this.context = context;
        this.metricsService = metricService;
        this.checks = checks;
    }

    /**
     * Gets all the checks to be executed.
     * @return all the checks to be executed
     */
    protected Collection<SecurityCheck> getChecks() {
        return checks;
    }

    protected Context getContext() {
        return context;
    }

    protected MetricsService getMetricsService() {
        return metricsService;
    }
}
