package org.aerogear.mobile.security.impl;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.Check;
import org.aerogear.mobile.security.SecurityCheck;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.ArrayList;

public class SecurityCheckExecutorImpl implements SecurityCheckExecutor {
    private final ArrayList<SecurityCheck> checks;
    private final Context context;

    private MetricsService metricsService;

    public SecurityCheckExecutorImpl(Context context) {
        this.checks = new ArrayList<>();
        this.context = context;
    }

    @Override
    public SecurityCheckExecutor addCheck(Check check) {
        checks.add(check.getSecurityCheck());
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
        ArrayList<SecurityCheckResult> results = new ArrayList<>();
        for (SecurityCheck check : checks) {
            results.add(check.test(context));
        }
        return buildResultArray(results);
    }

    private SecurityCheckResult[] buildResultArray(ArrayList<SecurityCheckResult> results) {
        return results.toArray(new SecurityCheckResult[results.size()]);
    }
}
