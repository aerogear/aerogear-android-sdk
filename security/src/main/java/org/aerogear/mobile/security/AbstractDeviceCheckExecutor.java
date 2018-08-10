package org.aerogear.mobile.security;


import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;


/**
 *
 * Base class for security check executors.
 *
 * @param <T> the real executor type to be returned to chain the adding of checks to be executed
 */
abstract class AbstractDeviceCheckExecutor<T extends AbstractDeviceCheckExecutor> {

    private final Collection<DeviceCheck> checks = new HashSet<>();
    private final Context context;
    private final MetricsService metricsService;

    /**
     * Creates AbstractDeviceCheckExecutor object.
     *
     * @param context {@link Context} to be used by the security checks
     * @param checks {@link Collection} of {@link DeviceCheck} to be executed
     * @param metricService {@link MetricsService} to be used to publish metrics. If null, no
     *        metrics get published
     * @throws IllegalArgumentException if context is null
     */
    public AbstractDeviceCheckExecutor(@NonNull final Context context,
                                       @NonNull final Collection<DeviceCheck> checks,
                                       @Nullable final MetricsService metricService) {
        this.context = nonNull(context, "context");
        this.checks.addAll(checks);
        this.metricsService = metricService;
    }

    /**
     * Gets all the checks to be executed.
     *
     * @return {@link Collection} of {@link DeviceCheck}
     */
    protected Collection<DeviceCheck> getChecks() {
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
     * Gets the metric published. It never returns null: if no metric service is present, e NOOP
     * publisher is returned.
     *
     * @return the metric service publisher
     */
    protected DeviceCheckExecutorListener getMetricServicePublisher() {
        if (metricsService != null) {
            return new DeviceCheckMetricPublisher(metricsService);
        } else {
            return new DeviceCheckExecutorListener() {
                @Override
                public void onExecuted(DeviceCheckResult result) {}

                @Override
                public void onComplete() {}
            };
        }
    }

    /**
     * Adds a new check to be executed.
     *
     * @param check the new {@link DeviceCheck} to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(DeviceCheck check) {
        this.getChecks().add(check);
        return (T) this;
    }

    /**
     * Adds a new check to be executed.
     *
     * @param checkType the {@link DeviceCheckType} of the new check to be executed
     * @return this, so that adding checks can be chained
     */
    public T addCheck(DeviceCheckType checkType) {
        this.getChecks().add(checkType.getDeviceCheck());
        return (T) this;
    }
}
