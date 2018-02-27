package org.aerogear.mobile.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Executor used to asynchronously execute checks.
 * Checks are executed by using {@link AppExecutors#singleThreadService()} if no custom executor is configured.
 */
public class AsyncSecurityCheckExecutor extends AbstractSecurityCheckExecutor<AsyncSecurityCheckExecutor> {

    /**
     * A {@link String} used to identify the activity being logged.
     */
    private final static String TAG = "AsyncSecurityCheckExecutor";

    /**
     * {@link Logger} used for logging.
     */
    private final static Logger LOG = MobileCore.getLogger();

    /**
     * Custom {@link ExecutorService} for AsyncSecurityCheckExecutor.
     */
    private final ExecutorService executorService;


    /**
     * Builder class for constructing an AsyncSecurityCheckExecutor object.
     */
    public static class Builder extends SecurityCheckExecutor.Builder.AbstractBuilder<Builder, AsyncSecurityCheckExecutor> {

        /**
         * Custom {@link ExecutorService} for AsyncSecurityCheckExecutor.
         */
        private ExecutorService executorService;

        /**
         * Creates a Builder object.
         *
         * @param ctx {@link Context} of the device.
         * @throws IllegalArgumentException if context is null.
         */
        Builder(final Context ctx) {
            super(ctx);
        }

        /**
         * Specify a custom execution singleThreadService for this SecurityCheckExecutor.
         *
         * @param executorService executor singleThreadService to be used.
         * @return this.
         */
        public Builder withExecutorService(@Nullable final ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * Creates a new AsyncSecurityCheckExecutor object.
         *
         * @return {@link AsyncSecurityCheckExecutor}.
         */
        @Override
        public AsyncSecurityCheckExecutor build() {
            if (executorService == null) {
                executorService = new AppExecutors().singleThreadService();
            }
            return new AsyncSecurityCheckExecutor(getCtx(), executorService, getChecks(), getMetricsService());
        }
    }

    /**
     * Constructor for AsyncSecurityCheckExecutor.
     *
     * @param context the {@link Context} of the device.
     * @param executorService the custom {@link ExecutorService}.
     * @param checks the {@link Collection<SecurityCheck>} of security checks to be tested.
     * @param metricsService {@link MetricsService}.
     * @throws IllegalArgumentException if context or executorService are null.
     */
    AsyncSecurityCheckExecutor(@NonNull final Context context,
                               @NonNull final ExecutorService executorService,
                               @NonNull final Collection<SecurityCheck> checks,
                               @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
        this.executorService = executorService;
    }

    /**
     * Executes the checks asynchronously.
     *
     * Returns a {@link Map} containing the results of each executed test (a {@link Future}).
     * The key of the map will be the output of {@link SecurityCheck#getName()}, while the value will be
     * a {@link Future} with the result of the check.
     *
     * @return {@link Map<String, Future<SecurityCheckResult>>}.
     */
    public Map<String, Future<SecurityCheckResult>> execute() {

        final MetricsService metricsService = getMetricsService();
        final Map<String, Future<SecurityCheckResult>> res = new HashMap<>();

        for (final SecurityCheck check : getChecks()) {
            res.put(check.getName(), (executorService.submit(() -> {
                final SecurityCheckResult result =  check.test(getContext());
                if (metricsService != null) {
                    metricsService.publish(new SecurityCheckResultMetric(result));
                }
                return result;
            })));
        }

        return res;
    }
}
