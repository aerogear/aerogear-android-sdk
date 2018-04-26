package org.aerogear.mobile.reactive;

/**
 * This method provides a function which will actually cancel a request. The default function is to
 * interrupt the thread which is running.
 */
public interface Canceller {
    void doCancel();
}
