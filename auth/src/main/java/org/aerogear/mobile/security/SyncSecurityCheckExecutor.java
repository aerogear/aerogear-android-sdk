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
import java.util.HashSet;

/**
 * Synchronously executes provided {@link SecurityCheck}s.
 */
public class SyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor {

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
     * @return the results of the executed checks
     */
    public SecurityCheckResult[] execute() {
        SecurityCheckResult[] results = getTestResults();
        if (getMetricsService() != null) {
            publishResultMetrics(results);
        }
        return getTestResults();
    }

    /**
     * Publish each {@link SecurityCheckResult result} provided as an {@link SecurityCheckResultMetric}.
     *
     * @param results Array of results
     */
    private void publishResultMetrics(final SecurityCheckResult[] results) {
        for(SecurityCheckResult result : results) {
            this.getMetricsService().publish(new SecurityCheckResultMetric(result));
        }
    }

    /**
     * Return the {@link SecurityCheckResult results} for each of the tests added.
     *
     * @return Array of results.
     */
    private SecurityCheckResult[] getTestResults() {

        Collection<SecurityCheck> checks = getChecks();

        SecurityCheckResult[] results = new SecurityCheckResult[checks.size()];
        int i = 0;
        for (SecurityCheck check : checks) {
            results[i++] = check.test(getContext());
        }
        return results;
    }
}
