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

    private final static String TAG = "AsyncSecurityCheckExecutor";

    private final ExecutorService executorService;
    private final static Logger LOG = MobileCore.getLogger();


    public static class Builder extends SecurityCheckExecutor.Builder.AbstractBuilder<Builder, AsyncSecurityCheckExecutor> {

        private ExecutorService executorService;

        public Builder(final Context ctx) {
            super(ctx);
        }

        /**
         * Specify a custom execution singleThreadService for this SecurityCheckExecutor.
         *
         * @param executorService executor singleThreadService to be used.
         * @return this
         */
        public Builder withExecutorService(@Nullable final ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        @Override
        public AsyncSecurityCheckExecutor build() {
            if (executorService == null) {
                executorService = new AppExecutors().singleThreadService();
            }
            return new AsyncSecurityCheckExecutor(getCtx(), executorService, getChecks(), getMetricsService());
        }
    }

    AsyncSecurityCheckExecutor(@NonNull final Context context,
                               @NonNull final ExecutorService executorService,
                               @NonNull final Collection<SecurityCheck> checks,
                               @Nullable final MetricsService metricsService) {
        super(context, checks, metricsService);
        this.executorService = executorService;
    }

    /**
     * Executes the checks asynchronously and returns an array of {@link Future}
     *
     * Returns a {@link Map} containing the results of each executed test (a {@link Future}).
     * The key of the map will be the output of {@link SecurityCheck#getName()}, while the value will be
     * a {@link Future} with the result of the check.
     *
     * @return a {@link Map} containing the results of all the executed checks
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
