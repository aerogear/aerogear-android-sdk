package org.aerogear.mobile.core;

import java.util.concurrent.Callable;

/**
 * This interface describes an implementation of the Subscriber Reactive pattern.
 * http://reactivex.io/documentation/single.html
 */
public interface Responder<T> {

    void onResult(T value);


    void onException(Exception e);
}
