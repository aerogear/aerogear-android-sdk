package org.aerogear.mobile.core.helper;

import org.aerogear.mobile.core.Callback;

import java.util.concurrent.Executors;

/**
 * This class will take a callback and return a value on that callback.  It is used for testing
 * reactive classes.
 */
public class AsyncWithCallback<T> {

    private final T emittedValue;

    public AsyncWithCallback(T emittedValue){
        this.emittedValue = emittedValue;
    }

    public void execute(Callback<T> callback) {
        Executors.newFixedThreadPool(1).submit(()->callback.onSuccess(emittedValue));
    }

}
