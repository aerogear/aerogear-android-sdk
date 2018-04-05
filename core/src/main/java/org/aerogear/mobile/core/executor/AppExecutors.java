package org.aerogear.mobile.core.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Inspired by
 * https://github.com/googlesamples/android-architecture/blob/todo-mvp/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/util/AppExecutors.java
 */
public final class AppExecutors {

    private static final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    private static final ExecutorService mainThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return Looper.getMainLooper().getThread();
        }
    });


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
