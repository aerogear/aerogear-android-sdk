package org.aerogear.mobile.security;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.impl.SecurityCheckExecutorImpl;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

public class SecurityService implements ServiceModule{
    private final static String TYPE = "security";

    private MobileCore core;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        this.core = core;
    }

    @Override
    public boolean requiresConfiguration() { return false; }

    @Override
    public void destroy() {}

    /**
     * Retrieve a {@link SecurityCheckExecutor} to run multiple {@link SecurityCheckType checks} chained.
     *
     * @return A new executor.
     */
    public SecurityCheckExecutor getCheckExecutor() {
        return new SecurityCheckExecutorImpl(core.getContext());
    }

    /**
     * Perform a single {@link SecurityCheckType} and get the {@link SecurityCheckResult result} for it.
     *
     * @param securityCheckType The check type to execute.
     * @return The result of the security check from the check type provided.
     */
    public SecurityCheckResult check(SecurityCheckType securityCheckType) {
        return securityCheckType.getSecurityCheck().test(core.getContext());
    }

    public SecurityCheckResult checkAndSendMetric(SecurityCheckType securityCheckType, MetricsService metricsService) {
        SecurityCheckResult result = check(securityCheckType);
        metricsService.publish(new SecurityCheckResultMetric(result));
        return result;
    }
}
