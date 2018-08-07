package org.aerogear.mobile.security;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

/**
 * Service for running security checks in an application.
 * <p>
 * Security checks can be run individually using {@link #check(SecurityCheckType)}. Security checks
 * can also be chained together to execute security checks synchronously or asynchronously. Invoking
 * {@link #getCheckExecutor()} will return a {@link SyncSecurityCheckExecutor} where security checks
 * can be executed synchronously. Invoking {@link #getAsyncCheckExecutor()} will return
 * {@link AsyncSecurityCheckExecutor} where security checks can be executed asynchronously.
 */
public class SecurityService {

    static final String SECURITY_METRICS_EVENT_TYPE = "security";

    /**
     * Retrieve a check executor that can synchronously run multiple security checks.
     *
     * @return {@link SyncSecurityCheckExecutor}
     */
    public SyncSecurityCheckExecutor getCheckExecutor() {
        return SecurityCheckExecutor.Builder.newSyncExecutor(MobileCore.getInstance().getContext())
                        .build();
    }

    /**
     * Retrieve a check executor that can asynchronously run multiple security checks.
     *
     * @return {@link AsyncSecurityCheckExecutor}
     */
    public AsyncSecurityCheckExecutor getAsyncCheckExecutor() {
        return SecurityCheckExecutor.Builder.newAsyncExecutor(MobileCore.getInstance().getContext())
                        .build();
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
        return nonNull(securityCheck, "securityCheck").test(MobileCore.getInstance().getContext());
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
