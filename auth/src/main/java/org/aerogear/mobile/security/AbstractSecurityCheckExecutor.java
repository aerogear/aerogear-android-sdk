package org.aerogear.mobile.security;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.Collection;
import java.util.HashSet;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Base class for security check executors.
 */
abstract class AbstractSecurityCheckExecutor<T extends AbstractSecurityCheckExecutor> {

    /**
     * Collection of checks to be executed. Cannot be null.
     */
    private final Collection<SecurityCheck> checks = new HashSet<>();

    /**
     * Context for the device. Cannot be null.
     */
    private final Context context;

    /**
     * Metric singleThreadService to be used to publish metrics. Can be null.
     */
    private final MetricsService metricsService;

    /**
     * Creates AbstractSecurityCheckExecutor object.
     *
     * @param context the context.
     * @param checks checks to be executed.
     * @param metricService singleThreadService to be used to publish metrics. If null, no metrics get published.
     * @throws IllegalArgumentException if context or checks are null.
     */
    public AbstractSecurityCheckExecutor(@NonNull final Context context,
                                         @NonNull final Collection<SecurityCheck> checks,
                                         @Nullable final MetricsService metricService) {
        this.context = nonNull(context, "context");
        this.metricsService = nonNull(metricService, "metricService");
        this.checks.addAll(checks);
    }

    /**
     * Gets all the checks to be executed.
     *
     * @return {@link Collection<SecurityCheck>}.
     */
    protected Collection<SecurityCheck> getChecks() {
        return checks;
    }

    /**
     * Gets the context for the device.
     *
     * @return {@link Context}.
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Gets the metrics service.
     *
     * @return {@link MetricsService}.
     */
    protected MetricsService getMetricsService() {
        return metricsService;
    }

    /**
     * Adds a new check to be executed.
     *
     * @param check the new {@link SecurityCheck} to be executed.
     * @return {@link T} this, so that adding checks can be chained.
     */
    public T addCheck(SecurityCheck check) {
        this.getChecks().add(check);
        return (T) this;
    }

    /**
     * Adds a new check to be executed.
     *
     * @param checkType the {@link SecurityCheckType} of the new check to be executed.
     * @return {@link T} this, so that adding checks can be chained.
     */
    public T addCheck(SecurityCheckType checkType) {
        this.getChecks().add(checkType.getSecurityCheck());
        return (T) this;
    }
}
