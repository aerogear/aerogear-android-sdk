package org.aerogear.mobile.core.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Inspired by
 * https://github.com/googlesamples/android-architecture/blob/todo-mvp/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/util/AppExecutors.java
 */
public final class AppExecutors {

    private static final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    private static final ExecutorService mainThreadExecutor = new AbstractExecutorService() {
        private final Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void shutdown() {
        }

        @NonNull
        @Override
        public List<Runnable> shutdownNow() {
            return new ArrayList<>();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long l, @NonNull TimeUnit timeUnit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(@NonNull Runnable runnable) {
            handler.post(runnable);
        }
    };

//        Executors.newSingleThreadExecutor(new ThreadFactory() {
//        @Override
//        public Thread newThread(@NonNull Runnable r) {
//            return Looper.getMainLooper().getThread();
//        }
//    });


    private static final ExecutorService serviceThreadExecutor =
                    Executors.newSingleThreadExecutor();

    public AppExecutors() {}

    public ExecutorService mainThread() {
        return mainThreadExecutor;
    }

    public ExecutorService networkThread() {
        return networkExecutor;
    }

    public ExecutorService singleThreadService() {
        return serviceThreadExecutor;
    }

}
