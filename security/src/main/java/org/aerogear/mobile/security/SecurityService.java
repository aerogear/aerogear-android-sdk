package org.aerogear.mobile.security;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

/**
 * Service for running security checks in an application.
 * <p>
 * Security checks can be run individually using {@link #check(DeviceCheckType)}. Security checks
 * can also be chained together to execute security checks synchronously or asynchronously. Invoking
 * {@link #getCheckExecutor()} will return a {@link SyncDeviceCheckExecutor} where security checks
 * can be executed synchronously. Invoking {@link #getAsyncCheckExecutor()} will return
 * {@link AsyncDeviceCheckExecutor} where security checks can be executed asynchronously.
 */
public class SecurityService {

    static final String SECURITY_METRICS_EVENT_TYPE = "security";

    /**
     * Retrieve a check executor that can synchronously run multiple security checks.
     *
     * @return {@link SyncDeviceCheckExecutor}
     */
    public SyncDeviceCheckExecutor getCheckExecutor() {
        return DeviceCheckExecutor.Builder.newSyncExecutor(MobileCore.getInstance().getContext())
                        .build();
    }

    /**
     * Retrieve a check executor that can asynchronously run multiple security checks.
     *
     * @return {@link AsyncDeviceCheckExecutor}
     */
    public AsyncDeviceCheckExecutor getAsyncCheckExecutor() {
        return DeviceCheckExecutor.Builder.newAsyncExecutor(MobileCore.getInstance().getContext())
                        .build();
    }

    /**
     * Used with enumeration to perform a single {@link DeviceCheckType} and get the
     * {@link DeviceCheckResult result} for it.
     *
     * @param deviceCheckType The {@link DeviceCheckType} to execute
     * @return {@link DeviceCheckResult}
     * @throws IllegalArgumentException if deviceCheckType is null
     */
    public DeviceCheckResult check(@NonNull final DeviceCheckType deviceCheckType) {
        return check(nonNull(deviceCheckType, "deviceCheckType").getSecurityCheck());
    }

    /**
     * Used with a custom check to perform a single {@link DeviceCheck} and get the
     * {@link DeviceCheckResult result} for it.
     *
     * @param deviceCheck The {@link DeviceCheck} to execute
     * @return {@link DeviceCheckResult}
     * @throws IllegalArgumentException if deviceCheck is null
     */
    public DeviceCheckResult check(@NonNull final DeviceCheck deviceCheck) {
        return nonNull(deviceCheck, "deviceCheck").test(MobileCore.getInstance().getContext());
    }

    /**
     * Perform a single {@link DeviceCheckType} , get the {@link DeviceCheckResult result} and
     * publish a {@link SecurityCheckResultMetric} based on the result.
     *
     * @param deviceCheckType The {@link DeviceCheckType} to execute
     * @param metricsService {@link MetricsService}
     * @return {@link DeviceCheckResult}
     */
    public DeviceCheckResult checkAndSendMetric(final DeviceCheckType deviceCheckType,
                                                final MetricsService metricsService) {
        return checkAndSendMetric(deviceCheckType.getSecurityCheck(), metricsService);
    }

    /**
     * Perform a single {@link DeviceCheck} and return a {@link DeviceCheckResult}.
     *
     * @param deviceCheck The {@link DeviceCheck} to execute
     * @param metricsService {@link MetricsService}
     * @return {@link DeviceCheckResult}
     * @throws IllegalArgumentException if metricsService is null
     */
    public DeviceCheckResult checkAndSendMetric(final DeviceCheck deviceCheck,
                                                @NonNull final MetricsService metricsService) {
        final DeviceCheckResult result = check(deviceCheck);
        nonNull(metricsService, "metricsService").publish(SECURITY_METRICS_EVENT_TYPE,
                        new SecurityCheckResultMetric(result));
        return result;
    }
}
