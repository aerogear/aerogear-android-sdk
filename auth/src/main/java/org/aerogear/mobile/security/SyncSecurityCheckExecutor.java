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

    SyncSecurityCheckExecutor(@NonNull final Context context,
                              @NonNull final Collection<SecurityCheck> checks,
                              @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
    }

    /**
     * Executes the provided checks and returns the results.
     * Blocks until all checks are executed.
     * <p>
     * Returns a {@link Map} containing the results of each executed test.
     * The key of the map will be the output of {@link SecurityCheck#getName()}, while the value will be
     * the result of the check.
     *
     * @return a {@link Map} containing the results of all the executed checks
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
     * Publish each {@link SecurityCheckResult result} provided as an {@link SecurityCheckResultMetric}.
     *
     * @param result result to be published
     */
    private void publishResultMetrics(@NonNull SecurityCheckResult result) {
        MetricsService metricsService = getMetricsService();

        if (metricsService != null) {
            metricsService.publish(new SecurityCheckResultMetric(result));
        }
    }

    public static class Builder extends SecurityCheckExecutor.Builder.AbstractBuilder<Builder, SyncSecurityCheckExecutor> {
        Builder(final Context ctx) {
            super(ctx);
        }

        @Override
        public SyncSecurityCheckExecutor build() {
            return new SyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
        }
    }
}
