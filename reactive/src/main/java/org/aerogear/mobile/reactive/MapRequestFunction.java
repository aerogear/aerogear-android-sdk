package org.aerogear.mobile.reactive;

@FunctionalInterface
/**
 * This interface defines a function which transforms a value of type T into type R. This is the
 * same as the Java Function interface, but is necessary to be compatible with more versions of
 * Android
 */
public interface MapRequestFunction<T, R, REQUEST extends Request<R>> {
    REQUEST map(T value)  throws Exception;
}
