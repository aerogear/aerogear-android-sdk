package org.aerogear.mobile.security;


import java.util.ArrayList;
import java.util.List;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

/**
 * This object will manage the communication with the metric service, batching the results to be
 * published.
 */
class SecurityCheckMetricPublisher implements SecurityCheckExecutorListener {

    private final MetricsService metricsService;
    private final List<SecurityCheckResult> metricResults = new ArrayList<>();

    /**
     * Builds the object.
     *
     * @param metricsService metric service to be used to publish the metrics
     */
    SecurityCheckMetricPublisher(final MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public synchronized void onExecuted(SecurityCheckResult result) {
        metricResults.add(result);
    }

    @Override
    public synchronized void onComplete() {
        metricsService.publish(SecurityService.SECURITY_METRICS_EVENT_TYPE,
            new SecurityCheckResultMetric(metricResults));
    }
}
