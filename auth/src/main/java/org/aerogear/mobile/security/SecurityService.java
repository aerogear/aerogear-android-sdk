package org.aerogear.mobile.security;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Service for running security checks in an application.
 *
 * Checks can be run individually using {@link #check(SecurityCheckType)} , or can be chained
 * together using an {@link SyncSecurityCheckExecutor} by using {@link #getCheckExecutor()}.
 */
public class SecurityService implements ServiceModule{
    private final static String TYPE = "security";

    private MobileCore core;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        this.core = core;
    }

    @Override
    public boolean requiresConfiguration() { return false; }

    @Override
    public void destroy() {}

    /**
     * Retrieve a {@link SyncSecurityCheckExecutor} to run multiple {@link SecurityCheckType checks} chained.
     *
     * @return A new executor.
     */
    public SyncSecurityCheckExecutor getCheckExecutor() {
        return SecurityCheckExecutor.Builder
            .newSyncExecutor(core.getContext()).build();
    }

    /**
     * Retrieve a {@link AsyncSecurityCheckExecutor} to asynchronously run multiple {@link SecurityCheckType checks} chained.
     *
     * @return A new async executor.
     */
    public AsyncSecurityCheckExecutor getAsyncCheckExecutor() {
        return SecurityCheckExecutor.Builder
            .newAsyncExecutor(core.getContext()).build();
    }

    /**
     * Perform a single {@link SecurityCheckType} and get the {@link SecurityCheckResult result} for it.
     *
     * @param securityCheckType The type of check to execute.
     * @return The result of the security check from the check type provided.
     */
    public SecurityCheckResult check(final SecurityCheckType securityCheckType) {
        return check(securityCheckType.getSecurityCheck());
    }

    /**
     * Perform a single {@link SecurityCheck} and get the {@link SecurityCheckResult result} for it.
     *
     * @param securityCheck The check to execute.
     * @return The result of the security check from the check provided.
     */
    public SecurityCheckResult check(final SecurityCheck securityCheck) {
        return securityCheck.test(core.getContext());
    }

    /**
     * Perform a single {@link SecurityCheckType} , get the {@link SecurityCheckResult result} and
     * publish a {@link SecurityCheckResultMetric} based on the result.
     *
     * @param securityCheckType The type of check to execute.
     * @return The result of the security check from the check type provided.
     */
    public SecurityCheckResult checkAndSendMetric(final SecurityCheckType securityCheckType, final MetricsService metricsService) {
        return checkAndSendMetric(securityCheckType.getSecurityCheck(), metricsService);
    }

    /**
     * Perform a single {@link SecurityCheck} , and return a {@link SecurityCheckResult}.
     *
     * @param securityCheck The check to execute.
     * @return The result of the security check from the check provided.
     */
    public SecurityCheckResult checkAndSendMetric(final SecurityCheck securityCheck, final MetricsService metricsService) {
        SecurityCheckResult result = check(securityCheck);
        metricsService.publish(new SecurityCheckResultMetric(result));
        return result;
    }
}
