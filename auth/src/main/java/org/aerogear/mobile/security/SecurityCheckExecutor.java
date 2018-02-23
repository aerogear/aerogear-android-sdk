package org.aerogear.mobile.security;


import android.content.Context;

import org.aerogear.mobile.core.metrics.MetricsService;

import java.util.ArrayList;
import java.util.List;

public class SecurityCheckExecutor {

    private SecurityCheckExecutor() {

    }

    public static class Builder {

        private static abstract class AbstractBuilder<T, K> {
            private final Context ctx;
            private final List<SecurityCheck> checks = new ArrayList<>();
            private MetricsService metricsService;

            public AbstractBuilder(final Context ctx) {
                this.ctx = ctx;
            }

            public T withSecurityCheck(final SecurityCheck check) {
                checks.add(check);
                return (T) this;
            }

            public T withSecurityCheck(final SecurityCheckType checkType) {
                checks.add(checkType.getSecurityCheck());
                return (T) this;
            }

            public T withMetricsService(final MetricsService metricsService) {
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

            public abstract K build();
        }

        public class AsyncExecutorBuilder extends AbstractBuilder<AsyncExecutorBuilder, AsyncSecurityCheckExecutor> {

            AsyncExecutorBuilder(final Context ctx) {
                super(ctx);
            }

            @Override
            public AsyncSecurityCheckExecutor build() {
                return new AsyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
            }
        }

        public class SyncExecutorBuilder extends AbstractBuilder<SyncExecutorBuilder, SyncSecurityCheckExecutor>{
            SyncExecutorBuilder(final Context ctx) {
                super(ctx);
            }

            @Override
            public SyncSecurityCheckExecutor build() {
                return new SyncSecurityCheckExecutor(getCtx(), getChecks(), getMetricsService());
            }
        }

        private AsyncExecutorBuilder newAsyncBuilder(final Context ctx) {
            return new AsyncExecutorBuilder(ctx);
        }

        private SyncExecutorBuilder newSyncBuilder(final Context ctx) {
            return new SyncExecutorBuilder(ctx);
        }

        public static AsyncExecutorBuilder newAsyncExecutor(final Context ctx) {
            SecurityCheckExecutor ex = new SecurityCheckExecutor();
            return new Builder().newAsyncBuilder(ctx);
        }

        public static SyncExecutorBuilder newSyncExecutor(final Context ctx) {
            return new Builder().newSyncBuilder(ctx);
        }
    }



}
