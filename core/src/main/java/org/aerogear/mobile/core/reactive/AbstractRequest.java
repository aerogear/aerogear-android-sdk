package org.aerogear.mobile.core.reactive;

import org.aerogear.mobile.core.Request;

import java.util.concurrent.ExecutorService;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * This class performs wrapping and checking for subclasses.
 */
public abstract class AbstractRequest<T> implements Request<T> {

    @Override
    public final Request<T> runOn(ExecutorService executorService) {
        nonNull(executorService, "executorService");
        return new RunOnRequest<T>(this, executorService);
    }
}
