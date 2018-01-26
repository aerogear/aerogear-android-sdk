package org.aerogear.mobile.core.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Inspired by https://github.com/googlesamples/android-architecture/blob/todo-mvp/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/util/AppExecutors.java
 */
public final class AppExecutors {

    private static final Executor networkExecutor = Executors.newSingleThreadExecutor();

    private static final Executor mainThreadExecutor = new Executor() {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    };

    public AppExecutors() {
    }


    public Executor mainThread() {
        return mainThreadExecutor;
    }

    public Executor networkThread() {
        return networkExecutor;
    }


}
