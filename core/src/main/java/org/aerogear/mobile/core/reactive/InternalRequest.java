package org.aerogear.mobile.core.reactive;

import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.Request;
import org.aerogear.mobile.core.Responder;

/**
 * This interface defines a set of methods that are internal to the reactive system. These methods
 * allow us to tightly couple implementation details of the various proxies, wrappers, delegates,
 * etc without exposing that implementation detail to outside packages.
 *
 * Unless you are adding classes to the org.aerogear.mobile.core.reactive package please us
 * {@link Request} instead.
 *
 * If android ever supports java 9 modules we will need to revisit this architecture.
 *
 * @param <T>
 */
interface InternalRequest<T> extends Request<T> {

    /**
     * This method is the internal implementation of {@link AbstractRequest#respondWith(Responder)}.
     *
     * The abstract class respondWith manages certain cross cutting concerns like null safety, state
     * management, etc. This method is for the mundane tasks that request implementations will need
     * to deal with.
     *
     * @param responderRef a reference to the responder. This reference may become null if the
     *        responder is disconnected.
     * @return a chainable instance of Request, not guaranteed to be `this`
     */
    Request<T> respondWithActual(@NonNull AtomicReference<Responder<T>> responderRef);
}
