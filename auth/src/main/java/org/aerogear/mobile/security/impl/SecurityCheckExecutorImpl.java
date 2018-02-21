package org.aerogear.mobile.security.impl;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class SecurityCheckExecutorImpl implements SecurityCheckExecutor {
    private final Collection<SecurityCheck> checks;
    private final Context context;

    private MetricsService metricsService;

    public SecurityCheckExecutorImpl(Context context) {
        this.checks = new HashSet<>();
        this.context = context;
    }

    @Override
    public SecurityCheckExecutor addCheck(SecurityCheckType securityCheckType) {
        return addCheck(securityCheckType.getSecurityCheck());
    }

    @Override
    public SecurityCheckExecutor addCheck(SecurityCheck check) {
        checks.add(check);
        return this;
    }

    @Override
    public SecurityCheckExecutor sendMetrics(MetricsService metricsService) {
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
    private void publishResultMetrics(SecurityCheckResult[] results) {
        for(SecurityCheckResult result : results) {
            this.metricsService.publish(new SecurityCheckResultMetric(result));
        }
    }

    /**
     * Return the {@link SecurityCheckResult results} for each of the tests added.
     *
     * @return Array of results.
     */
    private SecurityCheckResult[] getTestResults() {
        List<SecurityCheckResult> results = new ArrayList<>();
        for (SecurityCheck check : checks) {
            results.add(check.test(context));
        }
        return buildResultArray(results);
    }

    private SecurityCheckResult[] buildResultArray(List<SecurityCheckResult> results) {
        return results.toArray(new SecurityCheckResult[results.size()]);
    }
}
