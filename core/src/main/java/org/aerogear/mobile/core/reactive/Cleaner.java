package org.aerogear.mobile.core.reactive;

/**
 * This interface is used by a {@link Request} to cleanup any resources used by a request.  This
 * is guaranteed to be called once for every request that is actually executed.  This means that if
 * you call {@link Request#cache()} the method will be called after the value to be cached is generated.
 *
 */
@FunctionalInterface
public interface Cleaner {
    void cleanup();
}
