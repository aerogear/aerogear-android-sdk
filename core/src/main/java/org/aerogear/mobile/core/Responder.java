package org.aerogear.mobile.core;

import java.util.concurrent.Callable;

/**
 * This interface describes an implementation of the Subscriber Reactive pattern.
 * http://reactivex.io/documentation/single.html
 */
public interface Responder<T> {

    void onSuccess(T value);


}
