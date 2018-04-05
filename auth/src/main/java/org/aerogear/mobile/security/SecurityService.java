package org.aerogear.mobile.security;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

/**
 * Service for running security checks in an application.
 *
 * Security checks can be run individually using {@link #check(SecurityCheckType)}. Security checks
 * can also be chained together to execute security checks synchronously or asynchronously. Invoking
 * {@link #getCheckExecutor()} will return a {@link SyncSecurityCheckExecutor} where security checks
 * can be executed synchronously. Invoking {@link #getAsyncCheckExecutor()} will return
 * {@link AsyncSecurityCheckExecutor} where security checks can be executed asynchronously.
 */
public class SecurityService implements ServiceModule {

    public static final String SECURITY_METRICS_EVENT_TYPE = "security";
    private final static String TYPE = "security";

    private MobileCore core;

    /**
     * Gets the service type.
     *
     * @return {@link String}
     */
    @Override
    public String type() {
        return TYPE;
    }

    /**
     * Configures the security service.
     *
     * @param core {@link MobileCore} instance
     * @param serviceConfiguration {@link ServiceConfiguration} for the security service. Can be
     *        null
     */
    @Override
    public void configure(@NonNull final MobileCore core,
                    @Nullable final ServiceConfiguration serviceConfiguration) {
        this.core = nonNull(core, "core");
    }

    /**
     * Checks if the service requires a service configuration. This service does not require a
     * service configuration.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean requiresConfiguration() {
        return false;
    }

    /**
     * Invoked when security service needs to be destroyed.
     */
    @Override
    public void destroy() {}

    /**
     * Retrieve a check executor that can synchronously run multiple security checks.
     *
     * @return {@link SyncSecurityCheckExecutor}
     */
    public SyncSecurityCheckExecutor getCheckExecutor() {
        return SecurityCheckExecutor.Builder.newSyncExecutor(core.getContext()).build();
    }

    /**
     * Retrieve a check executor that can asynchronously run multiple security checks.
     *
     * @return {@link AsyncSecurityCheckExecutor}
     */
    public AsyncSecurityCheckExecutor getAsyncCheckExecutor() {
        return SecurityCheckExecutor.Builder.newAsyncExecutor(core.getContext()).build();
    }

    /**
     * Used with enumeration to perform a single {@link SecurityCheckType} and get the
     * {@link SecurityCheckResult result} for it.
     *
     * @param securityCheckType The {@link SecurityCheckType} to execute
     * @return {@link SecurityCheckResult}
     * @throws IllegalArgumentException if securityCheckType is null
     */
    public SecurityCheckResult check(@NonNull final SecurityCheckType securityCheckType) {
        return check(nonNull(securityCheckType, "securityCheckType").getSecurityCheck());
    }

    /**
     * Used with a custom check to perform a single {@link SecurityCheck} and get the
     * {@link SecurityCheckResult result} for it.
     *
     * @param securityCheck The {@link SecurityCheck} to execute
     * @return {@link SecurityCheckResult}
     * @throws IllegalArgumentException if securityCheck is null
     */
    public SecurityCheckResult check(@NonNull final SecurityCheck securityCheck) {
        return nonNull(securityCheck, "securityCheck").test(core.getContext());
    }

    /**
     * Perform a single {@link SecurityCheckType} , get the {@link SecurityCheckResult result} and
     * publish a {@link SecurityCheckResultMetric} based on the result.
     *
     * @param securityCheckType The {@link SecurityCheckType} to execute
     * @param metricsService {@link MetricsService}
     * @return {@link SecurityCheckResult}
     */
    public SecurityCheckResult checkAndSendMetric(final SecurityCheckType securityCheckType,
                    final MetricsService metricsService) {
        return checkAndSendMetric(securityCheckType.getSecurityCheck(), metricsService);
    }

    /**
     * Perform a single {@link SecurityCheck} and return a {@link SecurityCheckResult}.
     *
     * @param securityCheck The {@link SecurityCheck} to execute
     * @param metricsService {@link MetricsService}
     * @return {@link SecurityCheckResult}
     * @throws IllegalArgumentException if metricsService is null
     */
    public SecurityCheckResult checkAndSendMetric(final SecurityCheck securityCheck,
                    @NonNull final MetricsService metricsService) {
        final SecurityCheckResult result = check(securityCheck);
        nonNull(metricsService, "metricsService").publish(SECURITY_METRICS_EVENT_TYPE,
                        new SecurityCheckResultMetric(result));
        return result;
    }
}
