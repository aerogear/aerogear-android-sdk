package org.aerogear.mobile.security;

import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor {

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    AsyncSecurityCheckExecutor(final Context context, final Collection<SecurityCheck> checks, final MetricsService metricsService) {
        super(context, checks, metricsService);
    }

    public Future<SecurityCheckResult>[] execute() {

        final Collection<SecurityCheck> checks = getChecks();
        final MetricsService metricsService = getMetricsService();

        final Future[] res = new Future[checks.size()];

        int i = 0;
        for (final SecurityCheck check : checks) {
            res[i++] = (pool.submit(() -> {
                final SecurityCheckResult result =  check.test(getContext());
                if (metricsService != null) {
                    metricsService.publish(new SecurityCheckResultMetric(result));
                }
                return result;
            }));
        }

        return res;
    }
}
