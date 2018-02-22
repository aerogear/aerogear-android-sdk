package org.aerogear.mobile.security.impl;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashSet;

/**
 * Implementation of {@link SecurityCheckExecutor}.
 * <p>
 * If {@link #sendMetrics(MetricsService)} is invoked then upon {@link #execute()} each
 * {@link SecurityCheckResult result} will be converted to a {@link SecurityCheckResultMetric} and
 * published individually. Not as a batch.
 */
public class SecurityCheckExecutorImpl implements SecurityCheckExecutor {

    private final Collection<SecurityCheck> checks;
    private final Context context;

    private MetricsService metricsService;

    public SecurityCheckExecutorImpl(final Context context) {
        this.checks = new HashSet<>();
        this.context = context;
    }

    @Override
    public SecurityCheckExecutor addCheck(final SecurityCheckType securityCheckType) {
        return addCheck(securityCheckType.getSecurityCheck());
    }

    @Override
    public SecurityCheckExecutor addCheck(final SecurityCheck check) {
        checks.add(check);
        return this;
    }

    @Override
    public SecurityCheckExecutor sendMetrics(final MetricsService metricsService) {
        this.metricsService = metricsService;
        return this;
    }

    @Override
    public SecurityCheckResult[] execute() {
        SecurityCheckResult[] results = getTestResults();
        if (metricsService != null) {
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
        for (SecurityCheckResult result : results) {
            this.metricsService.publish(new SecurityCheckResultMetric(result));
        }
    }

    /**
     * Return the {@link SecurityCheckResult results} for each of the tests added.
     *
     * @return Array of results.
     */
    private SecurityCheckResult[] getTestResults() {
        SecurityCheckResult[] results = new SecurityCheckResult[checks.size()];
        int i = 0;
        for (SecurityCheck check : checks) {
            results[i++] = check.test(context);
        }
        return results;
    }
}
