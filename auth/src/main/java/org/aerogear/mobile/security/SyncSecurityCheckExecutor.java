package org.aerogear.mobile.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Synchronously executes provided {@link SecurityCheck}s.
 */
public class SyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor<SyncSecurityCheckExecutor> {

    /**
     * Builder class for constructing a SyncSecurityCheckExecutor object.
     */
    public static class Builder extends SecurityCheckExecutor.Builder.AbstractBuilder<Builder, SyncSecurityCheckExecutor> {
        Builder(final Context ctx) {
            super(ctx);
        }

        /**
         * Creates a new SyncSecurityCheckExecutor object.
         *
         * @return {@link SyncSecurityCheckExecutor}.
         */
        @Override
        public SyncSecurityCheckExecutor build() {
            return new SyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
        }
    }

    /**
     * Constructor for SyncSecurityCheckExecutor.
     *
     * @param context the {@link Context} of the device.
     * @param checks the {@link Collection<SecurityCheck>} of security checks to be tested.
     * @param metricsService {@link MetricsService}.
     * @throws IllegalArgumentException if context is null.
     */
    SyncSecurityCheckExecutor(@NonNull final Context context,
                              @NonNull final Collection<SecurityCheck> checks,
                              @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
    }

    /**
     * Executes the provided checks and returns the results.
     * Blocks until all checks are executed.
     *
     * Returns a {@link Map} containing the results of each executed test.
     * The key of the map will be the output of {@link SecurityCheck#getName()}, while the value will be
     * the result of the check.
     *
     * @return {@link Map<String, SecurityCheckResult>}.
     */
    public Map<String, SecurityCheckResult> execute() {
        final Map<String, SecurityCheckResult> results = new HashMap<>();

        for (SecurityCheck check : getChecks()) {
            SecurityCheckResult result = check.test(getContext());
            results.put(check.getName(), result);
            publishResultMetrics(result);
        }

        return results;
    }

    /**
     * Publish each result provided as an {@link SecurityCheckResultMetric}.
     *
     * @param result {@link SecurityCheckResult} to be published
     */
    private void publishResultMetrics(@NonNull SecurityCheckResult result) {
        MetricsService metricsService = getMetricsService();

        if (metricsService != null) {
            metricsService.publish(new SecurityCheckResultMetric(result));
        }
    }
}
