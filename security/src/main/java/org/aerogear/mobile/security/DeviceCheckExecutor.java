package org.aerogear.mobile.security;


import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.MetricsService;

/**
 * Entry point for the DeviceCheckExecutor. This class provides the builders.
 */
public class DeviceCheckExecutor {
    private DeviceCheckExecutor() {}

    /**
     * Entry point for DeviceCheckExecutor builders.
     */
    public static class Builder {

        /**
         * Base class for DeviceCheckExecutor builders.
         *
         * @param <T> The type of this builder
         * @param <K> The type of the built object
         */
        static abstract class AbstractBuilder<T, K> {

            private final Context ctx;
            private final Collection<DeviceCheck> checks = new HashSet<>();
            private MetricsService metricsService;

            /**
             * Creates AbstractBuilder object.
             *
             * @param ctx {@link Context} to be used by the security checks
             * @throws IllegalArgumentException if ctx is null
             */
            public AbstractBuilder(@NonNull final Context ctx) {
                this.ctx = nonNull(ctx, "context").getApplicationContext();
            }

            /**
             * Adds a new security check by providing a security check instance.
             *
             * @param check the {@link DeviceCheck} to be added
             * @return this
             * @throws IllegalArgumentException if check is null
             */
            public T withSecurityCheck(@NonNull final DeviceCheck check) {
                checks.add(nonNull(check, "check"));
                return (T) this;
            }

            /**
             * Adds a new security check by providing a {@link DeviceCheckType}
             *
             * @param checkType {@link DeviceCheckType} to be added
             * @return this
             * @throws IllegalArgumentException if checkType is null
             */
            public T withSecurityCheck(@NonNull final DeviceCheckType checkType) {
                checks.add(nonNull(checkType, "checkType").getDeviceCheck());
                return (T) this;
            }

            /**
             * Sets the metric service to be used. The metric service should be a
             * {@link AppExecutors#singleThreadService()}.
             *
             * @param metricsService the {@link MetricsService}
             *        {@link AppExecutors#singleThreadService()} to be used. Can be null
             * @return this
             */
            public T withMetricsService(@Nullable final MetricsService metricsService) {
                this.metricsService = metricsService;
                return (T) this;
            }

            /**
             * Gets the context for the device.
             *
             * @return {@link Context}
             */
            protected Context getCtx() {
                return ctx;
            }

            /**
             * Gets the metric service being used.
             *
             * @return {@link MetricsService}
             */
            protected MetricsService getMetricsService() {
                return metricsService;
            }

            /**
             * Gets all the checks that are to be tested.
             *
             * @return {@link Collection}
             */
            protected Collection<DeviceCheck> getChecks() {
                return checks;
            }

            /**
             * Builds the executor according to the passed in parameters.
             *
             * @return {@link K} the executor instance
             */
            public abstract K build();
        }

        /**
         * Creates a Builder for AsyncDeviceCheckExecutor.
         *
         * @param ctx {@link Context} to be used by the security checks
         * @return {@link AsyncDeviceCheckExecutor.Builder}
         */
        private AsyncDeviceCheckExecutor.Builder newAsyncBuilder(@NonNull final Context ctx) {
            return new AsyncDeviceCheckExecutor.Builder(ctx);
        }

        /**
         * Creates a Builder for SyncDeviceCheckExecutor.
         *
         * @param ctx {@link Context} to be used by the security checks
         * @return {@link SyncDeviceCheckExecutor.Builder}
         */
        private SyncDeviceCheckExecutor.Builder newSyncBuilder(@NonNull final Context ctx) {
            return new SyncDeviceCheckExecutor.Builder(ctx);
        }

        /**
         * Creates a new AsyncDeviceCheckExecutor Builder.
         *
         * @param ctx {@link Context} to be used by the security checks
         * @return {@link AsyncDeviceCheckExecutor.Builder}
         */
        public static AsyncDeviceCheckExecutor.Builder newAsyncExecutor(
                        @NonNull final Context ctx) {
            return new Builder().newAsyncBuilder(ctx);
        }

        /**
         * Creates a new SyncDeviceCheckExecutor Builder.
         *
         * @param ctx {@link Context} to be used by the security checks
         * @return {@link SyncDeviceCheckExecutor.Builder}
         */
        public static SyncDeviceCheckExecutor.Builder newSyncExecutor(
                        @NonNull final Context ctx) {
            return new Builder().newSyncBuilder(ctx);
        }
    }
}
