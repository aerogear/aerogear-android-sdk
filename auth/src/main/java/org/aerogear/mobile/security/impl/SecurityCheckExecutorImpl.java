package org.aerogear.mobile.security.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashSet;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Implementation of {@link SecurityCheckExecutor}.
 *
 * If {@link #sendMetrics(MetricsService)} is invoked then upon {@link #execute()} each
 * {@link SecurityCheckResult result} will be converted to a {@link SecurityCheckResultMetric} and
 * published individually. Not as a batch.
 */
public class SecurityCheckExecutorImpl implements SecurityCheckExecutor {
    private final Collection<SecurityCheck> checks;
    private final Context context;

    private MetricsService metricsService;

    public SecurityCheckExecutorImpl(@NonNull final Context context) {
        this.checks = new HashSet<>();
        this.context = nonNull(context, "context");
    }

    @Override
    public SecurityCheckExecutor addCheck(@NonNull final SecurityCheckType securityCheckType) {
        return addCheck(nonNull(securityCheckType, "securityCheckType").getSecurityCheck());
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
        final SecurityCheckResult[] results = getTestResults();
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
    private void publishResultMetrics(@NonNull final SecurityCheckResult[] results) {
        nonNull(results, "results");
        for(final SecurityCheckResult result : results) {
            this.metricsService.publish(new SecurityCheckResultMetric(result));
        }
    }

    /**
     * Return the {@link SecurityCheckResult results} for each of the tests added.
     *
     * @return Array of results.
     */
    private SecurityCheckResult[] getTestResults() {
        final SecurityCheckResult[] results = new SecurityCheckResult[checks.size()];
        int i = 0;
        for (final SecurityCheck check : checks) {
            results[i++] = check.test(context);
        }
        return results;
    }
}
