package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashSet;

/**
 * Implementation of {@link SyncSecurityCheckExecutor}.
 */
public class SyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor {

    SyncSecurityCheckExecutor(final Context context, final Collection<SecurityCheck> checks, final MetricsService metricsService) {
        super(context, checks, metricsService);
    }

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
