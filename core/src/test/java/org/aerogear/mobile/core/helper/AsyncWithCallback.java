package org.aerogear.mobile.core.helper;

import android.telecom.Call;

import org.aerogear.mobile.core.Callback;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * This class will take a callback and return a value on that callback.  It is used for testing
 * reactive classes.
 */
public class AsyncWithCallback<T> {

    private T emittedValue;
    private Callable<T> emittedCallable;

    public AsyncWithCallback(T emittedValue) {
        this.emittedValue = emittedValue;
    }

    public AsyncWithCallback(Callable<T> emittedCallable) {
        this.emittedCallable = emittedCallable;
    }

    public void execute(Callback<T> callback) {
        Executors.newFixedThreadPool(1).submit(() -> {

            try {
                if (emittedCallable != null) {
                    emittedValue = emittedCallable.call();
                }
                if (emittedValue instanceof Throwable) {
                    throw new RuntimeException(((Throwable) emittedValue).getMessage());
                }
                callback.onSuccess(emittedValue);
            } catch (Exception ex) {
                callback.onError(ex);
            }
        });
    }

}
