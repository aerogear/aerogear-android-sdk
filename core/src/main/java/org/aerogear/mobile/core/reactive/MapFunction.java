package org.aerogear.mobile.core.reactive;

@FunctionalInterface
public interface MapFunction<T, R> {
    R map(T value);
}
