package org.aerogear.mobile.security;

import org.aerogear.mobile.core.metrics.MetricsService;

public interface SecurityCheckExecutor {
    /**
     * Add a {@link SecurityCheckType check type} to be executed on {@link #execute()}.
     *
     * @param securityCheckType The check type to add.
     * @return {@link SecurityCheckExecutor}
     */
    SecurityCheckExecutor addCheck(SecurityCheckType securityCheckType);

    /**
     * Add a {@link SecurityCheck} to be executed on {@link #execute()}.
     *
     * @param check The security check to add.
     * @return {@link SecurityCheckExecutor}
     */
    SecurityCheckExecutor addCheck(SecurityCheck check);

    /**
     * Set that metrics should be sent on {@link #execute()} using the {@link MetricsService} provided.
     *
     * @return {@link SecurityCheckExecutor}
     */
    SecurityCheckExecutor sendMetrics(MetricsService metricsService);

    /**
     * Return the results of each test that was added to the executor.
     *
     * @return Array of {@link SecurityCheckResult results}
     */
    SecurityCheckResult[] execute();
}
