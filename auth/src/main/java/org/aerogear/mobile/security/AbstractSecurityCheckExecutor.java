package org.aerogear.mobile.security;


import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.Collection;
import java.util.HashSet;

abstract class AbstractSecurityCheckExecutor {
    private final Collection<SecurityCheck> checks;
    private final Context context;

    private final MetricsService metricsService;

    public AbstractSecurityCheckExecutor(final Context context, Collection<SecurityCheck> checks, MetricsService metricService) {
        this.context = context;
        this.metricsService = metricService;
        this.checks = checks;
    }

    protected Collection<SecurityCheck> getChecks() {
        return checks;
    }

    protected Context getContext() {
        return context;
    }

    protected MetricsService getMetricsService() {
        return metricsService;
    }
}
