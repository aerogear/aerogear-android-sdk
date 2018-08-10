package org.aerogear.mobile.security;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

/**
 * This object will manage the communication with the metric service, batching the results to be
 * published.
 */
class DeviceCheckMetricPublisher implements DeviceCheckExecutorListener {

    private final MetricsService metricsService;
    private final List<DeviceCheckResult> metricResults =
                    Collections.synchronizedList(new ArrayList<>());

    /**
     * Builds the object.
     *
     * @param metricsService metric service to be used to publish the metrics
     */
    DeviceCheckMetricPublisher(final MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    public void onExecuted(DeviceCheckResult result) {
        metricResults.add(result);
    }

    @Override
    public void onComplete() {
        metricsService.publish(SecurityService.SECURITY_METRICS_EVENT_TYPE,
                        new SecurityCheckResultMetric(metricResults))
                        .respondWith(new Responder<Boolean>() {
                            @Override
                            public void onResult(Boolean value) {
                                MobileCore.getLogger().debug("Metrics sent");
                            }

                            @Override
                            public void onException(Exception exception) {
                                MobileCore.getLogger().error("Metrics did not send", exception);
                            }
                        });
    }
}
