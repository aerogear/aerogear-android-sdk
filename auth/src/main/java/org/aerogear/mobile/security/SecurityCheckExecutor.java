package org.aerogear.mobile.security;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entry point for the SecurityCheckExecutor.
 * This class provides the builders.
 */
public class SecurityCheckExecutor {
    private SecurityCheckExecutor() {

    }

    /**
     * Entry point for SecurityCheckExecutor builders.
     */
    public static class Builder {

        /**
         * Base class for SecurityCheckExecutor builders
         * @param <T> The type of this builder
         * @param <K> The type of the built object
         */
        static abstract class AbstractBuilder<T, K> {
            private final Context ctx;
            private final List<SecurityCheck> checks = new ArrayList<>();
            private MetricsService metricsService;

            public AbstractBuilder(@NonNull final Context ctx) {
                this.ctx = ctx;
            }

            /**
             * Adds a new security check by providing a security check instance.
             *
             * @param check the check to be added
             * @return this
             */
            public T withSecurityCheck(@NonNull final SecurityCheck check) {
                checks.add(check);
                return (T) this;
            }

            /**
             * Adds a new security check.
             * @param checkType type of security check to be added
             * @return this
             */
            public T withSecurityCheck(@NonNull final SecurityCheckType checkType) {
                checks.add(checkType.getSecurityCheck());
                return (T) this;
            }

            /**
             * Sets the metric singleThreadService to be used.
             *
             * @param metricsService the metric singleThreadService to be used
             * @return this
             */
            public T withMetricsService(@Nullable final MetricsService metricsService) {
                this.metricsService = metricsService;
                return (T) this;
            }

            protected Context getCtx() {
                return ctx;
            }

            protected MetricsService getMetricsService() {
                return metricsService;
            }

            protected List<SecurityCheck> getChecks() {
                return checks;
            }

            /**
             * Builds the executor according to the passed in parameters.
             *
             * @return the executor instance
             */
            public abstract K build();
        }

        private AsyncSecurityCheckExecutor.Builder newAsyncBuilder(@NonNull final Context ctx) {
            return new AsyncSecurityCheckExecutor.Builder(ctx);
        }

        private SyncSecurityCheckExecutor.Builder newSyncBuilder(@NonNull final Context ctx) {
            return new SyncSecurityCheckExecutor.Builder(ctx);
        }

        /**
         * Creates a new AsyncExecutor Builder
         * @param ctx the context
         * @return the AsyncExecutor builder
         */
        public static AsyncSecurityCheckExecutor.Builder newAsyncExecutor(@NonNull final Context ctx) {
            return new Builder().newAsyncBuilder(ctx);
        }

        /**
         * Creates a new SyncExecutor Builder
         * @param ctx the context
         * @return the SyncExecutor builder
         */
        public static SyncSecurityCheckExecutor.Builder newSyncExecutor(@NonNull final Context ctx) {
            return new Builder().newSyncBuilder(ctx);
        }
    }
}
