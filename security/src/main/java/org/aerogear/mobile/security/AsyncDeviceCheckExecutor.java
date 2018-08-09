package org.aerogear.mobile.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.MetricsService;

/**
 * Executor used to asynchronously execute checks. Checks are executed by using
 * {@link AppExecutors#singleThreadService()} if no custom executor is configured.
 */
public class AsyncDeviceCheckExecutor
                extends AbstractDeviceCheckExecutor<AsyncDeviceCheckExecutor> {

    private final ExecutorService executorService;


    /**
     * Builder class for constructing an AsyncDeviceCheckExecutor object.
     */
    public static class Builder extends
                    DeviceCheckExecutor.Builder.AbstractBuilder<Builder, AsyncDeviceCheckExecutor> {

        private ExecutorService executorService;

        /**
         * Creates a Builder object.
         *
         * @param ctx {@link Context} to be used by security checks
         */
        Builder(final Context ctx) {
            super(ctx);
        }

        /**
         * A custom {@link ExecutorService} for this DeviceCheckExecutor.
         *
         * @param executorService the {@link ExecutorService} to be used. Defaults to
         *        {@link AppExecutors#singleThreadService()} if null
         * @return this
         */
        public Builder withExecutorService(@Nullable final ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * Creates a new AsyncDeviceCheckExecutor object.
         *
         * If no {@link ExecutorService} has been defined, defaults to
         * {@link AppExecutors#singleThreadService()}.
         *
         * @return {@link AsyncDeviceCheckExecutor}
         */
        @Override
        public AsyncDeviceCheckExecutor build() {
            if (executorService == null) {
                executorService = new AppExecutors().singleThreadService();
            }
            return new AsyncDeviceCheckExecutor(getCtx(), executorService, getChecks(),
                            getMetricsService());
        }
    }

    /**
     * Constructor for AsyncDeviceCheckExecutor.
     *
     * @param context the {@link Context} to be used by security checks
     * @param executorService the custom {@link ExecutorService}
     * @param checks the {@link Collection< DeviceCheck >} of security checks to be tested
     * @param metricsService {@link MetricsService}. Can be null
     */
    AsyncDeviceCheckExecutor(@NonNull final Context context,
                             @NonNull final ExecutorService executorService,
                             @NonNull final Collection<DeviceCheck> checks,
                             @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
        this.executorService = executorService;
    }

    /**
     * Executes the checks asynchronously.
     *
     * Returns a {@link Map} containing the results of each executed test (a {@link Future}). The
     * key of the map will be the output of {@link DeviceCheck#getId()}, while the value will be a
     * {@link Map} of {@link Future} with the {@link DeviceCheckResult} of the check.
     *
     * @return {@link Map}
     */
    public Map<String, Future<DeviceCheckResult>> execute() {
        return execute(new DeviceCheckExecutorListener[0]);
    }

    /**
     * Executes the checks asynchronously.
     *
     * Returns a {@link Map} containing the results of each executed test (a {@link Future}). The
     * key of the map will be the output of {@link DeviceCheck#getId()}, while the value will be a
     * {@link Map} of {@link Future} with the {@link DeviceCheckResult} of the check.
     *
     * @param deviceCheckExecutorListeners list of listeners that will receive events about checks
     *        execution
     * @return {@link Map}
     */
    private Map<String, Future<DeviceCheckResult>> execute(
                    DeviceCheckExecutorListener... deviceCheckExecutorListeners) {

        final List<DeviceCheckExecutorListener> listeners = deviceCheckExecutorListeners == null
                        ? new ArrayList<>(1)
                        : new ArrayList<>(Arrays.asList(deviceCheckExecutorListeners));
        final Collection<DeviceCheck> checks = getChecks();

        // Adds the metric publisher to the passed in listeners
        listeners.add(getMetricServicePublisher());

        final Map<String, Future<DeviceCheckResult>> res = new HashMap<>();
        final AtomicInteger count = new AtomicInteger(checks.size());

        for (final DeviceCheck check : checks) {
            res.put(check.getId(), (executorService.submit(() -> {
                final DeviceCheckResult result = check.test(getContext());

                final int remaining = count.decrementAndGet();

                for (DeviceCheckExecutorListener listener : listeners) {
                    listener.onExecuted(result);

                    if (remaining <= 0) {
                        listener.onComplete();
                    }
                }

                return result;
            })));
        }

        return res;
    }
}
