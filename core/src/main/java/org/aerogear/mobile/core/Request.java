package org.aerogear.mobile.core;

/**
 * This interface describes an implementation of the Single Observable Reactive pattern.
 * http://reactivex.io/documentation/single.html
 */
public interface Request<T> {


    Request<T> respondWith(Responder<T> responder);
}
