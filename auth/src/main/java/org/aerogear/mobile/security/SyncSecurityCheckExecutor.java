package org.aerogear.mobile.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Synchronously executes provided {@link SecurityCheck}s.
 */
public class SyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor<SyncSecurityCheckExecutor> {

    public static class Builder extends SecurityCheckExecutor.Builder.AbstractBuilder<Builder, SyncSecurityCheckExecutor> {
        Builder(final Context ctx) {
            super(ctx);
        }

        @Override
        public SyncSecurityCheckExecutor build() {
            return new SyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
        }
    }

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
     * @return a {@link Map} containing the results of all the executed checks
     */
    public Map<String, SecurityCheckResult> execute() {
        final Map<String, SecurityCheckResult> results = getTestResults();
        if (getMetricsService() != null) {
            publishResultMetrics(results.values());
        }
        return getTestResults();
    }

    /**
     * Publish each {@link SecurityCheckResult result} provided as an {@link SecurityCheckResultMetric}.
     *
     * @param results Collection of results
     */
    private void publishResultMetrics(@NonNull final Collection <SecurityCheckResult> results) {
        for(SecurityCheckResult result : results) {
            this.getMetricsService().publish(new SecurityCheckResultMetric(result));
        }
    }

    /**
     * Returns a {@link Map} containing the results of each executed test.
     * The key of the map will be the output of {@link SecurityCheck#getName()}, while the value will be
     * the result of the check.
     *
     * @return a {@link Map} containing the results of all the executed checks
     */
    private Map<String, SecurityCheckResult> getTestResults() {

        final Map<String, SecurityCheckResult> results = new HashMap<>();

        for (SecurityCheck check : getChecks()) {
            results.put(check.getName(), check.test(getContext()));
        }
        return results;
    }
}
