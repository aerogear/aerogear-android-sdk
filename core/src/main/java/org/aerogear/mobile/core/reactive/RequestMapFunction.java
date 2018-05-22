package org.aerogear.mobile.core.reactive;

@FunctionalInterface
/**
 * This interface defines a function which transforms a value of type T into type R. This is the
 * same as the Java Function interface, but is necessary to be compatible with more versions of
 * Android
 */
public interface RequestMapFunction<T, R> {
    Request<R> map(T value) throws Exception;
}
