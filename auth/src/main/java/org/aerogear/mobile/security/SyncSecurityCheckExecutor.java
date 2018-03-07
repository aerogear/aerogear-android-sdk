package org.aerogear.mobile.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

/**
 * Synchronously executes provided {@link SecurityCheck}s.
 */
public class SyncSecurityCheckExecutor
                extends AbstractSecurityCheckExecutor<SyncSecurityCheckExecutor> {

    /**
     * Builder class for constructing a SyncSecurityCheckExecutor object.
     */
    public static class Builder extends
                    SecurityCheckExecutor.Builder.AbstractBuilder<Builder, SyncSecurityCheckExecutor> {

        /**
         * Creates a Builder object.
         *
         * @param ctx {@link Context} to be used by security checks
         * @throws IllegalArgumentException if ctx is null
         */
        Builder(final Context ctx) {
            super(ctx);
        }

        /**
         * Creates a new SyncSecurityCheckExecutor object.
         *
         * @return {@link SyncSecurityCheckExecutor}
         */
        @Override
        public SyncSecurityCheckExecutor build() {
            return new SyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
        }
    }

    /**
     * Constructor for SyncSecurityCheckExecutor.
     *
     * @param context the {@link Context} to be used by security checks
     * @param checks the {@link Collection} of security checks to be tested
     * @param metricsService {@link MetricsService}. Can be null
     */
    SyncSecurityCheckExecutor(@NonNull final Context context,
                    @NonNull final Collection<SecurityCheck> checks,
                    @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
    }

    /**
     * Executes the provided checks and returns the results. Blocks until all checks are executed.
     *
     * Returns a {@link Map} containing the results of each executed test. The key of the map will
     * be the output of {@link SecurityCheck#getId()}, while the value will be the
     * {@link SecurityCheckResult} of the check.
     *
     * @return {@link Map}
     */
    public Map<String, SecurityCheckResult> execute() {
        final Map<String, SecurityCheckResult> results = new HashMap<>();

        final SecurityCheckExecutorListener metricServicePublisher = getMetricServicePublisher();

        for (SecurityCheck check : getChecks()) {
            SecurityCheckResult result = check.test(getContext());
            results.put(check.getId(), result);

            metricServicePublisher.onExecuted(result);
        }

        metricServicePublisher.onComplete();

        return results;
    }
}
