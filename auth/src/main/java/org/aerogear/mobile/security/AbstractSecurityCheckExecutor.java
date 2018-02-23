package org.aerogear.mobile.security;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.Collection;
import java.util.HashSet;

/**
 * Base class for security check executors.
 */
abstract class AbstractSecurityCheckExecutor<T extends AbstractSecurityCheckExecutor> {

    /**
     * Collection of checks to be executed.
     */
    private final Collection<SecurityCheck> checks = new HashSet<>();

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
        this.checks.addAll(checks);
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

    /**
     * Adds a new check to be executed
     * @param check the new check to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(SecurityCheck check) {
        this.getChecks().add(check);
        return (T) this;
    }

    /**
     * Adds a new check to be executed
     * @param checkType the type of the new check to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(SecurityCheckType checkType) {
        this.getChecks().add(checkType.getSecurityCheck());
        return (T) this;
    }
}
