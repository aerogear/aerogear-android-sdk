package org.aerogear.mobile.security;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.Collection;
import java.util.HashSet;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;


/**
 *
 * Base class for security check executors.
 *
 * @param <T> the real executor type to be returned to chain the adding of checks to be executed
 */
abstract class AbstractSecurityCheckExecutor<T extends AbstractSecurityCheckExecutor> {

    private final Collection<SecurityCheck> checks = new HashSet<>();
    private final Context context;
    private final MetricsService metricsService;

    /**
     * Creates AbstractSecurityCheckExecutor object.
     *
     * @param context {@link Context} to be used by the security checks
     * @param checks  {@link Collection} of {@link SecurityCheck} to be executed
     * @param metricService {@link MetricsService} to be used to publish metrics. If null, no metrics get published
     * @throws IllegalArgumentException if context is null
     */
    public AbstractSecurityCheckExecutor(@NonNull final Context context,
                                         @NonNull final Collection<SecurityCheck> checks,
                                         @Nullable final MetricsService metricService) {
        this.context = nonNull(context, "context");
        this.checks.addAll(checks);
        this.metricsService = metricService;
    }

    /**
     * Gets all the checks to be executed.
     *
     * @return {@link Collection} of {@link SecurityCheck}
     */
    protected Collection<SecurityCheck> getChecks() {
        return checks;
    }

    /**
     * Gets the context for the device.
     *
     * @return {@link Context}
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Gets the metrics service.
     *
     * @return {@link MetricsService}
     */
    protected MetricsService getMetricsService() {
        return metricsService;
    }

    /**
     * Adds a new check to be executed.
     *
     * @param check the new {@link SecurityCheck} to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(SecurityCheck check) {
        this.getChecks().add(check);
        return (T) this;
    }

    /**
     * Adds a new check to be executed.
     *
     * @param checkType the {@link SecurityCheckType} of the new check to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(SecurityCheckType checkType) {
        this.getChecks().add(checkType.getSecurityCheck());
        return (T) this;
    }
}
